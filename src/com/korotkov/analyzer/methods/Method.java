package com.korotkov.analyzer.methods;

import com.korotkov.base.URICallInfo;

import java.util.List;

public enum Method {
    CANTINA {
        @Override
        public AbstractAnalyzeMethod getInstance(List<URICallInfo> uriCallInfos) {
            return new CantinaAnalyzeMethod(uriCallInfos);
        }
    }, FORMS {
        @Override
        public AbstractAnalyzeMethod getInstance(List<URICallInfo> uriCallInfos) {
            return new FormsAlalyzeMethod(uriCallInfos);
        }
    }, DOTS {
        @Override
        public AbstractAnalyzeMethod getInstance(List<URICallInfo> uriCallInfos) {
            return new DotsAnalyzeMethod(uriCallInfos);
        }
    };

    public AbstractAnalyzeMethod getInstance(List<URICallInfo> uriCallInfos) {
        return null;
    }
}
