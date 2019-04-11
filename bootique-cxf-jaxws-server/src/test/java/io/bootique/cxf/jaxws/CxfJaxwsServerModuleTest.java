package io.bootique.cxf.jaxws;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.ProvidesIntoSet;
import io.bootique.BQRuntime;
import io.bootique.test.junit.BQTestFactory;
import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import javax.xml.ws.Endpoint;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

public class CxfJaxwsServerModuleTest {


    @Rule
    public BQTestFactory testFactory = new BQTestFactory().autoLoadModules();

    public static class EndpointFactoryProvider1 implements Module {


        @Override
        public void configure(Binder binder) {
        }

        @ProvidesIntoSet
        @Singleton
        public Endpoint provideEndpoint(Bus bus) {
            EndpointImpl endpoint = new EndpointImpl(bus, new HelloWorldImpl());
            endpoint.publish("/factory1");
            return endpoint;
        }
    }

    public static class EndpointFactoryProvider2 implements Module {

        @Override
        public void configure(Binder binder) {
        }

        @ProvidesIntoSet
        @Singleton
        public Endpoint provideEndpoint(Bus bus) {
            EndpointImpl endpoint = new EndpointImpl(bus, new HelloWorldImpl());
            endpoint.publish("/factory2");
            return endpoint;
        }
    }



    @Test
    public void testProvideEndpointsFromDifferentModules() {
        BQRuntime runtime = testFactory.app()
                .module(EndpointFactoryProvider1.class)
                .module(EndpointFactoryProvider2.class)
                .module(binder -> CxfJaxwsServerModule.extend(binder).addEndpoint(() -> Endpoint.publish("/provider1", new HelloWorldImpl())))
                .module(binder -> CxfJaxwsServerModule.extend(binder).addEndpoint(() -> Endpoint.publish("/provider2", new HelloWorldImpl())))
                .createRuntime();

        List<String> publishedEndpoints = getEndpoints(runtime).stream()
                .map(endpoint -> (EndpointImpl) endpoint)
                .map(EndpointImpl::getAddress)
                .collect(Collectors.toList());

        Assert.assertTrue(publishedEndpoints.containsAll(asList("/factory1", "/factory2", "/provider1", "/provider2")));
    }

    private Set<Endpoint> getEndpoints(BQRuntime runtime) {
        return runtime.getInstance(Key.get(new TypeLiteral<Set<Endpoint>>() {}));
    }
}
