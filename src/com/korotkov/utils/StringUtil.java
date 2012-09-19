package com.korotkov.utils;

import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.util.List;

public class StringUtil {
    private StringUtil() {
    }

    public static String getDomainName(URI uri) {
        return extractName(uri.getAuthority());
    }

    private static String extractName(String host) {
        if (host == null) {
            return null;
        }

        int index = host.lastIndexOf('.');
        if (index == -1) {
            //maybe IP
            return host;
        }
        host = host.substring(0, index);

        index = host.lastIndexOf('.');
        return index == -1 ? host : host.substring(index + 1);
    }

    public static String removeBlankCharacters(String message) {
        return message.replaceAll("\\s+", "");
    }

    public static String removeTags(String message) {
        return message.replaceAll("<.*?>", "");
    }

    public static String removeNonLetters(String message) {
        message = message.toLowerCase();
        return message.replaceAll("[^a-zа-я]", "");
    }

    public static String toString(Reader in) throws IOException {
        final char[] buffer = new char[0x10000];
        StringBuilder out = new StringBuilder();
        int read;
        do {
            read = in.read(buffer, 0, buffer.length);
            if (read > 0) {
                out.append(buffer, 0, read);
            }
        } while (read >= 0);

        return out.toString();
    }

    public static String toString(List<String> signature) {
        StringBuilder result = new StringBuilder();
        for (String item : signature) {
            result.append(item).append('\n');
        }
        return result.toString();
    }
}
