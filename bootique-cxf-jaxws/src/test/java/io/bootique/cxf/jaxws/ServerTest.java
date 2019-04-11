package io.bootique.cxf.jaxws;


import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Singleton;
import io.bootique.cxf.CxfModule;
import io.bootique.test.junit.BQTestFactory;
import org.apache.cxf.transport.common.gzip.GZIPFeature;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import javax.xml.ws.Endpoint;

public class ServerTest {

    @ClassRule
    public static BQTestFactory TEST_FACTORY = new BQTestFactory().autoLoadModules();

    @BeforeClass
    public static void startJetty() {


    }

    public static class TestModule implements Module {

        @Override
        public void configure(Binder binder) {
            CxfModule.extend(binder).addFeature(GZIPFeature.class);
            CxfJaxwsServerModule.extend(binder).addEndpoint(() -> Endpoint.publish("/test", new HelloWorldImpl()));
        }



    }



    @Test
    public void testServer() throws Exception {

        TEST_FACTORY.app("-s")
                .module(b -> {
                    b.bind(HelloWorldImpl.class).in(Singleton.class);
                }).modules(TestModule.class)
                .createRuntime().run();

        Thread.sleep(1000000);


    }
}
