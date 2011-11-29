package com.korotkov.services;

import com.korotkov.utils.StringUtil;
import com.korotkov.utils.URIUtil;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GoogleService {
    private GoogleService() {
    }

    public static List<String> getDomains(List<String> q) {
        try {
            return process(q);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<String>();
    }

    private static List<String> process(List<String> q) throws IOException {
        final DefaultHttpClient httpclient = new DefaultHttpClient();
        final HttpUriRequest httpGet = new HttpGet(getGoogleQuery(q));

        HttpContext httpContext = new BasicHttpContext();
        httpContext.setAttribute(CoreProtocolPNames.USER_AGENT, "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.2 (KHTML, like Gecko) Chrome/15.0.874.106 Safari/535.2");
        httpGet.setHeader("Referer", "http://www.google.com");

        HttpResponse response = httpclient.execute(httpGet, httpContext);

        return findDomainsFromResult(response.getEntity().getContent());
    }

    private static String getGoogleQuery(List<String> q) throws UnsupportedEncodingException {
        StringBuilder builder = new StringBuilder("http://www.google.com/search?q=");
        boolean firstToken = true;
        for (String token : q) {
            if (!firstToken) {
                builder.append('+');
            }
            builder.append(URLEncoder.encode(token, "UTF-8"));
            firstToken = false;
        }
        return builder.toString();
    }

    private static List<String> findDomainsFromResult(InputStream content) {
        List<String> result = new ArrayList<String>();

        Scanner scanner = new Scanner(content);
        Pattern googleResultPattern = Pattern.compile("<cite>(.*?)</cite>");

        String match = scanner.findWithinHorizon(googleResultPattern, 0);
        while (match != null) {
            Matcher matcher = googleResultPattern.matcher(match);
            if (matcher.find()) {
                String domain = extractDomainFromResultURL(matcher.group(1));
                if (domain != null) {
                    result.add(domain);
                }
            }
            match = scanner.findWithinHorizon(googleResultPattern, 0);
        }

        return result;
    }

    private static String extractDomainFromResultURL(String url) {
        url = StringUtil.removeTags(url);
        try {
            URI uri = URIUtil.createURI(url);
            return StringUtil.getDomainName(uri);
        } catch (URISyntaxException e) {
            return null;
        }
    }
}
