package com.korotkov.base;

import com.korotkov.utils.StringUtil;
import org.jsoup.nodes.Document;

import java.net.URI;

public class URICallInfo {
    private final URI uri;
    private final Document document;

    public URICallInfo(URI uri, Document document) {
        this.uri = uri;
        this.document = document;
    }

    public URI getUri() {
        return uri;
    }

    public Document getDocument() {
        return document;
    }

    public String getDomainName() {
        return StringUtil.getDomainName(getUri());
    }
}
