package com.korotkov.utils;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.net.URI;
import java.net.URISyntaxException;

public class StringUtilTest extends Assert {
    @DataProvider(name = "pairs")
    public Object[][] getURIData() throws URISyntaxException {
        return new Object[][]{
                {"example", new URI("http://example.com/index.php")},
                {"example", new URI("http://www.example.com/index.php")},
                {"239:239:239:239", new URI("http://239:239:239:239/index.php")}
        };
    }

    @Test(dataProvider = "pairs")
    public void verifyDomain(String domain, URI uri) {
        assertEquals(StringUtil.getDomainName(uri), domain);
    }
}
