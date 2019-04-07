package io.bootique.cxf.jaxws;


import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Singleton;
import io.bootique.cxf.CxfModule;
import io.bootique.test.junit.BQTestFactory;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.transport.common.gzip.GZIPFeature;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Ignore;
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
    @Ignore
    public void testServer() throws Exception {

        TEST_FACTORY.app("-s")
                .module( b -> {
                    b.bind(HelloWorldImpl.class).in(Singleton.class);
                    CxfModule.extend(b).addFeature(LoggingFeature.class);
                    CxfJaxwsServerModule.extend(b).contributeServerInterceptors().addInFaultInterceptor(new LoggingInInterceptor());
                }).modules(TestModule.class)
                .createRuntime().run();

        Thread.sleep(1000000);


    }
}
