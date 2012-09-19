package com.korotkov;

import com.korotkov.analyzer.methods.CantinaAnalyzeMethod;
import com.korotkov.utils.StringUtil;
import com.korotkov.utils.TFUtil;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class SignatureTest extends Assert {
    void doTest(String fileName, List<String> expectedSignature) throws FileNotFoundException {
        TFUtil.init(new FileInputStream(new File("data", "tfData.txt")));
        final Scanner scanner = new Scanner(new File("testData", fileName));
        CantinaAnalyzeMethod cantinaAnalyzeMethod = new CantinaAnalyzeMethod(TFUtil.findWordsInFile(scanner));
        List<String> signature = cantinaAnalyzeMethod.getSignature();
        assertEquals(StringUtil.toString(signature), StringUtil.toString(expectedSignature));
    }

    @Test
    public void testHash() throws FileNotFoundException {
        doTest("hash.txt", Arrays.asList("hash", "hash", "hash", "hash", "hash"));
    }
}
