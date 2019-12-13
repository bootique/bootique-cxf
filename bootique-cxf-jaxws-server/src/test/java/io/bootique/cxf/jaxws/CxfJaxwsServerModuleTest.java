package io.bootique.cxf.jaxws;

import io.bootique.BQRuntime;
import io.bootique.di.Key;
import io.bootique.di.TypeLiteral;
import io.bootique.test.junit.BQTestFactory;
import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.xml.ws.Endpoint;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

public class CxfJaxwsServerModuleTest {

    @Rule
    public BQTestFactory testFactory = new BQTestFactory().autoLoadModules();

    public static class EndpointFactoryProvider1 implements Provider<Endpoint> {

        private final Bus bus;

        @Inject
        public EndpointFactoryProvider1(Bus bus) {
            this.bus = bus;
        }

        @Override
        public Endpoint get() {
            EndpointImpl endpoint = new EndpointImpl(bus, new HelloWorldImpl());
            endpoint.publish("/factory1");
            return endpoint;
        }
    }

    public static class EndpointFactoryProvider2 implements Provider<Endpoint> {
        private final Bus bus;

        @Inject
        public EndpointFactoryProvider2(Bus bus) {
            this.bus = bus;
        }

        public Endpoint get() {
            EndpointImpl endpoint = new EndpointImpl(bus, new HelloWorldImpl());
            endpoint.publish("/factory2");
            return endpoint;
        }
    }

    @Test
    public void testProvideEndpointsFromDifferentModules() {
        BQRuntime runtime = testFactory.app()
                .module(binder -> CxfJaxwsServerModule.extend(binder)
                        .addEndpoint(EndpointFactoryProvider1.class)
                        .addEndpoint(EndpointFactoryProvider2.class)
                        .addEndpoint(() -> Endpoint.publish("/provider1", new HelloWorldImpl()))
                        .addEndpoint(() -> Endpoint.publish("/provider2", new HelloWorldImpl()))
                )
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
