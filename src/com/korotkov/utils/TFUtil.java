package com.korotkov.utils;

import java.io.InputStream;
import java.util.*;
import java.util.regex.Pattern;

public class TFUtil {
    private static HashMap<String, Double> map = new HashMap<String, Double>();
    private static Double max = 0.0;

    private TFUtil() {
    }

    public static void init(InputStream inputStream) {
        if (map.size() > 0) {
            throw new Error("Already inited");
        }

        map = new HashMap<String, Double>();
        Scanner scanner = new Scanner(inputStream);
        while (scanner.hasNext()) {
            String key = scanner.next();
            double value = scanner.nextDouble();

            map.put(key, value);

            max = Math.max(max, value);
        }
    }

    public static double getTF(String term) {
        Double tf = map.get(term);
        if (tf == null) {
            tf = 0.0;
        }
        return tf;
    }

    public static double getNormalizedTF(String term) {
        return getTF(term) / max;
    }

    public static Map<String, Double> convertToTF(Map<String, Long> map) {
        double sum = 0;
        for (long value : map.values()) {
            sum += value;
        }

        HashMap<String, Double> result = new HashMap<String, Double>();

        for (Map.Entry<String, Long> entry : map.entrySet()) {
            result.put(entry.getKey(), entry.getValue() / sum);
        }

        return result;
    }

    public static Double getTFIDF(String term, Double value) {
        double normalizedTF = getNormalizedTF(term);
        if (normalizedTF == 0.0) {
            return 0.0;
        }
        return value / normalizedTF;
    }

    public static List<String> findWordsInFile(Scanner scanner) {
        final List<String> words = new ArrayList<String>();
        final Pattern wordPattern = Pattern.compile("\\w+");
        while (scanner.hasNext(wordPattern)) {
            String word = scanner.next(wordPattern);
            words.add(word.toLowerCase());
        }
        return words;
    }
}
