package com.korotkov.analyzer;

import com.korotkov.analyzer.methods.Method;
import com.korotkov.base.URICallInfo;

import java.util.HashMap;
import java.util.List;

public class ContentAnalyzer {
    public static HashMap<Method, Double> methods = new HashMap<Method, Double>();

    /*
     * Weights based on table 2 from paper
     */
    static {
        methods.put(Method.CANTINA, 6.0 / 9.0);
        methods.put(Method.FORMS, 1.0 / 9.0);
        methods.put(Method.DOTS, 2.0 / 9.0);
    }

    public static double getCoefficient(List<URICallInfo> uriCallInfos) {
        double result = 0;
        for (Method method : methods.keySet()) {
            double coefficient = methods.get(method);
            double probability = method.getInstance(uriCallInfos).getProbability();
            result += coefficient * probability;
        }
        return result;
    }
}
