package com.korotkov.analyzer.methods;

import com.korotkov.base.URICallInfo;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;

public class FormsAlalyzeMethod extends AbstractAnalyzeMethod {
    protected FormsAlalyzeMethod(List<URICallInfo> uriCallInfos) {
        super(uriCallInfos);
    }

    @Override
    public double getProbability() {
        Elements elements = getLastCall().getDocument().getElementsByTag("input");
        for (Element element : elements) {
            String type = element.attr("type");
            if ("password".equals(type)) {
                return 1.0;
            }
        }
        return 0.0;
    }
}
