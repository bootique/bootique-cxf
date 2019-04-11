package io.bootique.cxf.jaxws;


import com.google.inject.*;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.multibindings.ProvidesIntoSet;
import io.bootique.cxf.CxfModule;
import io.bootique.cxf.CxfModuleExtender;
import io.bootique.test.junit.BQTestFactory;
import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.feature.Feature;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.apache.cxf.transport.common.gzip.GZIPFeature;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import javax.jws.WebService;
import javax.xml.ws.Endpoint;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
            binder.bind(Server.class).toProvider(() -> {
                JaxWsServerFactoryBean bean = new JaxWsServerFactoryBean();
                bean.setAddress("/test");
                bean.setServiceBean(new HelloWorldImpl());
                return bean.create();
            }
            ).asEagerSingleton();
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
