package io.bootique.cxf.jaxws;


import com.google.inject.*;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.multibindings.ProvidesIntoSet;
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

            Multibinder<Feature> featureMultibinder = Multibinder.newSetBinder(binder, Feature.class);
            featureMultibinder.addBinding().to(GZIPFeature.class);
        }

        @ProvidesIntoSet
        @Singleton
        public Endpoint helloModule (HelloWorldImpl implementer, Bus bus) {
            Endpoint endpoint = new EndpointImpl(bus, implementer);
            endpoint.publish("/test");
            return endpoint;
        }

        @Provides
        @Singleton
        public List<Feature> provideFeatures(Set<Feature> featureSet) {
            return new ArrayList<>(featureSet);
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
