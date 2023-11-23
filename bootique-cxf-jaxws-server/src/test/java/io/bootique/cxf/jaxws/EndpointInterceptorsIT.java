package io.bootique.cxf.jaxws;

import io.bootique.BQRuntime;
import io.bootique.di.Key;
import io.bootique.di.TypeLiteral;
import io.bootique.junit5.BQTest;
import io.bootique.junit5.BQTestFactory;
import io.bootique.junit5.BQTestTool;
import org.apache.cxf.Bus;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.message.Message;
import org.junit.jupiter.api.Test;

import javax.xml.ws.Endpoint;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;

@BQTest
public class EndpointInterceptorsIT {

    static class NullInterceptor implements Interceptor<Message> {

        private final String test;

        NullInterceptor(String test) {
            this.test = test;
        }

        @Override
        public void handleMessage(Message message) throws Fault {

        }

        @Override
        public void handleFault(Message message) {

        }

        @Override
        public String toString() {
            return "NullInterceptor{" + "test='" + test + '\'' + '}';
        }
    }

    @BQTestTool
    final BQTestFactory testFactory = new BQTestFactory().autoLoadModules();

    @Test
    public void noEndpointInterceptors() {
        BQRuntime runtime = testFactory.app()
                .module(binder -> CxfJaxwsServerModule.extend(binder).addEndpoint(() -> Endpoint.publish("/test", new HelloWorldImpl())))
                .createRuntime();


        Set<Endpoint> endpoints = getEndpoints(runtime);

        endpoints.forEach(endpoint -> {
            EndpointImpl cxfEndpoint = (EndpointImpl) endpoint;

            assertTrue(cxfEndpoint.getInInterceptors().isEmpty());
            assertTrue(cxfEndpoint.getOutInterceptors().isEmpty());
            assertTrue(cxfEndpoint.getInFaultInterceptors().isEmpty());
            assertTrue(cxfEndpoint.getOutFaultInterceptors().isEmpty());
        });
    }

    private Set<Endpoint> getEndpoints(BQRuntime runtime) {
        return runtime.getInstance(Key.get(new TypeLiteral<Set<Endpoint>>() {
        }));
    }


    @Test
    public void endpointInterceptors() {


        NullInterceptor in = new NullInterceptor("in");
        NullInterceptor inFault = new NullInterceptor("inFault");
        NullInterceptor out = new NullInterceptor("out");
        NullInterceptor outFault = new NullInterceptor("outFault");

        NullInterceptor in_1 = new NullInterceptor("in_1");
        NullInterceptor inFault_1 = new NullInterceptor("inFault_1");
        NullInterceptor out_1 = new NullInterceptor("out_1");
        NullInterceptor outFault_1 = new NullInterceptor("outFault_1");

        List<Interceptor<Message>> endpointInterceptors = asList(in, out, inFault, out, outFault, in_1, out_1, inFault_1, outFault_1);


        BQRuntime runtime = testFactory.app().module(binder -> CxfJaxwsServerModule.extend(binder)
                .addEndpoint(() -> {
                    EndpointImpl endpoint = new EndpointImpl(new HelloWorldImpl());
                    endpoint.publish("/test");
                    return endpoint;
                })
                .addEndpoint(() -> {
                    EndpointImpl endpoint = new EndpointImpl(new HelloWorldImpl());
                    endpoint.getInInterceptors().add(in_1);
                    endpoint.getInFaultInterceptors().add(inFault_1);
                    endpoint.getOutInterceptors().add(out_1);
                    endpoint.getOutFaultInterceptors().add(outFault_1);

                    endpoint.publish("/test_withInterceptors");
                    return endpoint;
                })
                .contributeServerInterceptors()
                .addInInterceptor(in)
                .addOutInterceptor(out)
                .addInFaultInterceptor(inFault)
                .addOutFaultInterceptor(outFault)).createRuntime();
        Bus bus = runtime.getInstance(Bus.class);

        // check that server endpoints are not leaking to bus
        assertTrue(Collections.disjoint(bus.getInFaultInterceptors(), endpointInterceptors));
        assertTrue(Collections.disjoint(bus.getInInterceptors(), endpointInterceptors));
        assertTrue(Collections.disjoint(bus.getOutFaultInterceptors(), endpointInterceptors));
        assertTrue(Collections.disjoint(bus.getOutInterceptors(), endpointInterceptors));

        getEndpoints(runtime).forEach(endpoint -> {
            EndpointImpl cxfEndpoint = (EndpointImpl) endpoint;
            if (cxfEndpoint.getAddress().equals("/test_withInterceptors")) {
                assertEndpointConfiguration(cxfEndpoint, asList(in_1, in), asList(out_1, out), asList(inFault_1, inFault), asList(outFault_1, outFault));
            } else {
                assertEndpointConfiguration(cxfEndpoint, asList(in), asList(out), asList(inFault), asList(outFault));
            }
        });
    }

    private <T extends Interceptor> void assertEndpointConfiguration(
            EndpointImpl endpoint,
            Collection<T> in,
            Collection<T> out,
            Collection<T> inFault,
            Collection<T> outFault

    ) {
        assertArrayEquals(endpoint.getInInterceptors().toArray(), in.toArray());
        assertArrayEquals(endpoint.getInFaultInterceptors().toArray(), inFault.toArray());
        assertArrayEquals(endpoint.getOutInterceptors().toArray(), out.toArray());
        assertArrayEquals(endpoint.getOutFaultInterceptors().toArray(), outFault.toArray());
    }
}
