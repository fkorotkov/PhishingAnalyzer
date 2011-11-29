package com.korotkov.resolver;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.net.URI;
import java.net.URISyntaxException;

public class RedirectResolverTest extends Assert {
    @DataProvider(name = "pairs")
    public Object[][] getExtractData() {
        return new Object[][]{
                {"../index.php", "{test();\n location =  \"../index.php\" }"},
                {"../index.php", "{ location.href =\t \"../index.php\" }"},
                {"../index.php", "{ location.reload \t ( \n \"../index.php\" }"},
                {"../index.php", "{ location.replace \t ( \n \"../index.php\" }"}
        };
    }

    @DataProvider(name = "triples")
    public Object[][] getResolveData() throws URISyntaxException {
        return new Object[][]{
                {new URI("http://example.com/simple/test.php"), "../index.php", new URI("http://example.com/index.php")},
                {new URI("http://example.com/simple/test.php"), "../", new URI("http://example.com/")},
                {new URI("http://example.com/simple/test.php"), "../../index.php", null},
                {new URI("http://example.com/test.php"), "http://acme.com/", new URI("http://acme.com/")}
        };
    }

    @Test(dataProvider = "pairs")
    public void verifyExtract(String location, String script) {
        assertEquals(RedirectResolver.extractLocationFromJS(script), location);
    }

    @Test(dataProvider = "triples")
    public void verifyResolve(URI uri, String location, URI expected) {
        assertEquals(RedirectResolver.resolveNewLocation(uri, location), expected);
    }

}
