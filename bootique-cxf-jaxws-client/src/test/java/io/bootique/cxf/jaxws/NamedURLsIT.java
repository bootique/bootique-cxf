package io.bootique.cxf.jaxws;

import io.bootique.BQRuntime;
import io.bootique.di.Key;
import io.bootique.di.TypeLiteral;
import io.bootique.junit5.BQTest;
import io.bootique.junit5.BQTestFactory;
import io.bootique.junit5.BQTestTool;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@BQTest
public class NamedURLsIT {

    @BQTestTool
    final BQTestFactory testFactory = new BQTestFactory().autoLoadModules();

    static final TypeLiteral<Map<String, URL>> urlsMapTypeLiteral = new TypeLiteral<Map<String, URL>>() {
    };

    @Test
    public void noAdditionalConfiguration() {
        BQRuntime runtime = testFactory.app().createRuntime();
        Map<String, URL> namedURLs = runtime.getInstance(Key.get(urlsMapTypeLiteral, NamedURLs.class));
        assertTrue(namedURLs.isEmpty());
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
        assertEquals(2, namedURLs.size());
        assertEquals("http://simple.com/", namedURLs.get("simple").toString());
        assertEquals("http://example.com/ws?wsdl", namedURLs.get("wsdl").toString());
    }
}
