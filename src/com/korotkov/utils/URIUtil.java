package com.korotkov.utils;

import java.net.URI;
import java.net.URISyntaxException;

public class URIUtil {
    private URIUtil() {
    }

    public static URI createURI(String url) throws URISyntaxException {
        URI uri = new URI(url);
        if (uri.getScheme() == null) {
            uri = new URI("http://" + url);
        }
        return uri;
    }
}
