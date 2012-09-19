package com.korotkov.analyzer.methods;

import com.korotkov.utils.MathUtil;
import com.korotkov.utils.StringUtil;
import com.korotkov.utils.TFUtil;

import java.io.IOException;
import java.util.*;

public class CantinaAnalyzeMethod {
    private static final int N = 5;
    private final List<String> words;

    public CantinaAnalyzeMethod(List<String> words) {
        this.words = words;
    }

    public List<String> getWords() {
        return words;
    }

    public List<String> getSignature() {
        Map<String, Double> frequencyMap = buildTFMapFromWords();

        if (frequencyMap.size() == 0) {
            return Collections.emptyList();
        }

        return getSignatureFromMap(frequencyMap);
    }

    private Map<String, Double> buildTFMapFromWords() {
        Map<String, Long> map = new HashMap<String, Long>();

        for (String word : getWords()) {
            if (word.length() < 3) {
                continue;
            }
            Long value = map.get(word);
            if (value == null) {
                value = 0L;
            }
            map.put(word, value + 1);
        }

        return TFUtil.convertToTF(map);
    }

    private List<String> getSignatureFromMap(Map<String, Double> frequencyMap) {
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
