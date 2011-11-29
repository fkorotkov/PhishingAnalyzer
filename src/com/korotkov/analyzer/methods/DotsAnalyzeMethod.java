package com.korotkov.analyzer.methods;

import com.korotkov.base.URICallInfo;

import java.util.List;

public class DotsAnalyzeMethod extends AbstractAnalyzeMethod {
    protected DotsAnalyzeMethod(List<URICallInfo> uriCallInfos) {
        super(uriCallInfos);
    }

    @Override
    public double getProbability() {
        String url = getLastCall().getUri().toString();
        String[] parts = url.split("\\.");
        boolean good = parts == null || parts.length < 6;
        return good ? 0.0 : 1.0;
    }
}
