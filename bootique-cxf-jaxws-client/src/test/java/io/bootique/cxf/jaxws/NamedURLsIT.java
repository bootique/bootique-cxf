package io.bootique.cxf.jaxws;

import io.bootique.BQRuntime;
import io.bootique.di.Key;
import io.bootique.di.TypeLiteral;
import io.bootique.test.junit.BQTestFactory;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import java.net.URL;
import java.util.Map;

public class NamedURLsIT {

    @Rule
    public BQTestFactory testFactory = new BQTestFactory().autoLoadModules();

    private TypeLiteral<Map<String, URL>> urlsMapTypeLiteral = new TypeLiteral<Map<String, URL>>() {};

    @Test
    public void noAdditionalConfiguration() {
        BQRuntime runtime = testFactory.app().createRuntime();
        Map<String, URL> namedURLs = runtime.getInstance(Key.get(urlsMapTypeLiteral, NamedURLs.class));
        Assert.assertTrue(namedURLs.isEmpty());
    }

    @Test
    public void configurationFromFile() {
        BQRuntime runtime = testFactory.app("--config=classpath:named-urls.yaml").createRuntime();

        // config from file
        //        cxfjaxwsclient:
        //          urls:
        //              simple: http://simple.com/
        //              wsdl: http://example.com/ws?wsdl

        Map<String, URL> namedURLs = runtime.getInstance(Key.get(urlsMapTypeLiteral, NamedURLs.class));
        Assert.assertEquals(2, namedURLs.size());
        Assert.assertEquals("http://simple.com/", namedURLs.get("simple").toString());
        Assert.assertEquals("http://example.com/ws?wsdl", namedURLs.get("wsdl").toString());
    }
}
