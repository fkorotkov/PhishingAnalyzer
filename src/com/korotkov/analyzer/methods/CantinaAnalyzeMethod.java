package com.korotkov.analyzer.methods;

import com.korotkov.base.URICallInfo;
import com.korotkov.services.GoogleService;
import com.korotkov.utils.MathUtil;
import com.korotkov.utils.StringUtil;
import com.korotkov.utils.TFUtil;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.*;

class CantinaAnalyzeMethod extends AbstractAnalyzeMethod {
    private static final int N = 5;

    protected CantinaAnalyzeMethod(List<URICallInfo> uriCallInfos) {
        super(uriCallInfos);
    }

    @Override
    public double getProbability() {
        try {
            return getProbabilityImpl();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private double getProbabilityImpl() throws IOException {
        URICallInfo uriCallInfo = getLastCall();
        Map<String, Double> frequencyMap = buildTFMapFromHtmlContent(uriCallInfo.getDocument());

        if (frequencyMap.size() == 0) {
            //Zero content Means Phishing
            return 1.0;
        }

        List<String> signature = getSignature(frequencyMap);

        if (isSignatureStrange(signature)) {
            return 1.0;
        }

        signature.add(0, getLastCall().getDomainName());

        List<String> domains = GoogleService.getDomains(signature);

        if (domains.size() == 0) {
            //Zero results Means Phishing
            return 1.0;
        }
        return domains.contains(getLastCall().getDomainName()) ? 0.0 : 1.0;
    }

    private Map<String, Double> buildTFMapFromHtmlContent(Document document) throws IOException {
        Map<String, Long> map = new HashMap<String, Long>();

        StringTokenizer stringTokenizer = new StringTokenizer(document.text());
        while (stringTokenizer.hasMoreTokens()) {
            String token = stringTokenizer.nextToken();
            token = token.toLowerCase();
            token = StringUtil.removeBlankCharacters(token);
            if (token.length() < 2) {
                continue;
            }
            Long value = map.get(token);
            if (value == null) {
                value = 0L;
            }
            map.put(token, value + 1);
        }

        return TFUtil.convertToTF(map);
    }

    private List<String> getSignature(Map<String, Double> frequencyMap) {
        final Map<String, Double> tfidfMap = new HashMap<String, Double>(frequencyMap.size());

        for (Map.Entry<String, Double> entry : frequencyMap.entrySet()) {
            tfidfMap.put(entry.getKey(), TFUtil.getTFIDF(entry.getKey(), entry.getValue()));
        }

        List<String> entries = new ArrayList<String>(tfidfMap.keySet());
        Collections.sort(entries, new Comparator<String>() {
            public int compare(String o1, String o2) {
                return MathUtil.signum(tfidfMap.get(o2) - tfidfMap.get(o1));
            }
        });

        return entries.subList(0, Math.min(N, entries.size()));
    }

    private boolean isSignatureStrange(List<String> signature) {
        for (String token : signature) {
            token = StringUtil.removeNonLetters(token);
            if (token.length() > 2) {
                return false;
            }
        }
        return true;
    }
}
