package com.korotkov.analyzer.methods;

import com.korotkov.base.URICallInfo;

import java.util.List;

public abstract class AbstractAnalyzeMethod {
    private List<URICallInfo> uriCallInfos;

    protected AbstractAnalyzeMethod(List<URICallInfo> uriCallInfos) {
        this.uriCallInfos = uriCallInfos;
    }

    public URICallInfo getLastCall() {
        if (uriCallInfos != null && uriCallInfos.size() > 0) {
            return uriCallInfos.get(uriCallInfos.size() - 1);
        }
        return null;
    }

    /**
     * @return probability from 0.0 to 1. that a URL is a spam
     */
    public abstract double getProbability();
}
