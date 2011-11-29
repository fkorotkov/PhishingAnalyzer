package com.korotkov.resolver;

import com.korotkov.base.URICallInfo;
import com.korotkov.utils.StringUtil;
import com.korotkov.utils.URIUtil;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RedirectResolver {
    private final URI uri;

    public RedirectResolver(URI uri) {
        this.uri = uri;
    }

    public List<URICallInfo> resolve() throws URISyntaxException {
        try {
            return resolveURIs(uri);
        } catch (UnknownHostException e) {
            //banned
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static List<URICallInfo> resolveURIs(URI root) throws IOException, URISyntaxException {
        List<URICallInfo> uriCallInfos = new ArrayList<URICallInfo>();

        URI currentURI = root;

        outer:
        while (currentURI != null) {
            final DefaultHttpClient httpclient = new DefaultHttpClient();
            final HttpUriRequest httpGet = new HttpGet(currentURI);
            final HttpParams params = httpGet.getParams();
            params.setParameter("http.protocol.handle-redirects", false);
            httpGet.setParams(params);

            HttpResponse response = httpclient.execute(httpGet);

            String chatSet = resolveCharSet(response.getEntity().getContentType());
            Reader contentReader = new InputStreamReader(response.getEntity().getContent());
            if (chatSet != null) {
                contentReader = new InputStreamReader(response.getEntity().getContent(), chatSet);
            }
            String content = StringUtil.toString(contentReader);
            Document document = Jsoup.parse(content);


            uriCallInfos.add(new URICallInfo(currentURI, document));

            if (isRedirectCode(response.getStatusLine().getStatusCode())) {
                Header[] locationHeader = response.getHeaders("location");
                currentURI = URIUtil.createURI(locationHeader[0].getValue());
                continue;
            }

            Elements elements = document.getElementsByTag("script");

            for (Element element : elements) {
                String location = extractLocationFromJS(element.toString());
                if (location != null) {
                    currentURI = resolveNewLocation(currentURI, location);
                    continue outer;
                }
            }

            break;
        }

        return uriCallInfos;
    }

    private static String resolveCharSet(Header contentType) {
        for (HeaderElement element : contentType.getElements()) {
            if (element.getParameterByName("charset") != null) {
                return element.getParameterByName("charset").getValue();
            }
        }
        return null;
    }

    static URI resolveNewLocation(URI currentURI, String location) {
        if (location.startsWith("../")) {
            URI result = currentURI.resolve(location);
            String path = result.getPath();
            if (path != null && path.startsWith("/../")) {
                return null;
            }
            return result;
        }
        try {
            return URIUtil.createURI(location);
        } catch (URISyntaxException e) {
            return null;
        }
    }

    private static boolean isRedirectCode(int code) {
        return 300 <= code && code < 400;
    }

    static String extractLocationFromJS(String script) {
        script = StringUtil.removeBlankCharacters(script);

        List<Pattern> patternList = new ArrayList<Pattern>();

        //location="http://example.com";
        patternList.add(Pattern.compile("location=\"(.*)\""));

        //document.location.href="http://example.com";
        patternList.add(Pattern.compile("location\\.href=\"(.*)\""));

        //window.location.reload("http://example.com");
        patternList.add(Pattern.compile("location\\.reload\\(\"(.*)\""));

        //document.location.replace("http://example.com");
        patternList.add(Pattern.compile("location\\.replace\\(\"(.*)\""));

        return extractLocationFromJS(script, patternList);
    }

    private static String extractLocationFromJS(String script, List<Pattern> patternList) {
        for (Pattern pattern : patternList) {
            Matcher matcher = pattern.matcher(script);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        return null;
    }
}
