package com.korotkov;

import com.korotkov.analyzer.ContentAnalyzer;
import com.korotkov.base.URICallInfo;
import com.korotkov.resolver.RedirectResolver;
import com.korotkov.utils.TFUtil;
import com.korotkov.utils.URIUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String... args) throws FileNotFoundException {
        if (args.length > 0) {
            initRF(args[0]);
        }

        Scanner scanner = new Scanner(System.in);
        if (args.length > 1) {
            scanner = new Scanner(new FileInputStream(args[1]));
        }
        while (scanner.hasNext()) {
            try {
                processURL(scanner.nextLine());
                System.out.println();
            } catch (URISyntaxException e) {
                System.out.println("Bad URL");
            }
        }
        scanner.close();
    }

    private static void initRF(String fileName) {
        InputStream inputStream;
        try {
            inputStream = new FileInputStream(new File(fileName));
            TFUtil.init(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void processURL(String url) throws URISyntaxException {
        if (url == null) {
            return;
        }
        URI uri = URIUtil.createURI(url);
        RedirectResolver resolver = new RedirectResolver(uri);
        List<URICallInfo> uriCallInfos = resolver.resolve();
        if (uriCallInfos == null) {
            System.out.println("seems banned: " + url);
            return;
        }
        double coefficient = ContentAnalyzer.getCoefficient(uriCallInfos);
        System.out.println("redirect path:");
        for (URICallInfo uriCallInfo : uriCallInfos) {
            System.out.println(uriCallInfo.getUri().toString());
        }
        long percents = (long) Math.floor(100.0 * coefficient);
        System.out.println(percents + "% bad");
    }
}
