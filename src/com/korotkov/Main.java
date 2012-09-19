package com.korotkov;

import com.korotkov.analyzer.methods.CantinaAnalyzeMethod;
import com.korotkov.utils.TFUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Pattern;

public class Main {
    public static void main(String... args) throws FileNotFoundException {
        if (args.length < 2) {
            System.out.println("Bad arguments! <reverse frequency map file> <secrete words file> <file to analyze>");
            return;
        }
        initRF(args[0]);
        final Set<String> strangeWords = getStrangeWords(args[1]);
        List<String> wordsInDocument = TFUtil.findWordsInFile(new Scanner(new FileInputStream(args[2])));

        analyze(wordsInDocument, strangeWords);
    }

    private static Set<String> getStrangeWords(String fileName) throws FileNotFoundException {
        List<String> wordsInFile = TFUtil.findWordsInFile(new Scanner(new FileInputStream(fileName)));
        return new HashSet<String>(wordsInFile);
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

    private static void analyze(List<String> wordsInDocument, Set<String> strangeWords) {
        CantinaAnalyzeMethod cantinaAnalyzeMethod = new CantinaAnalyzeMethod(wordsInDocument);
        List<String> signature = cantinaAnalyzeMethod.getSignature();
        for (String signatureWord : signature) {
            if(strangeWords.contains(signatureWord)) {
                System.out.println("Secrete document");
            }
        }
        System.out.println("Regular document");
    }
}
