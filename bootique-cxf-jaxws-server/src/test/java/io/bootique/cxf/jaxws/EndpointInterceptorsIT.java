package io.bootique.cxf.jaxws;

import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import io.bootique.BQRuntime;
import io.bootique.test.junit.BQTestFactory;
import org.apache.cxf.Bus;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.message.Message;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import javax.xml.ws.Endpoint;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;

public class EndpointInterceptorsIT {


    static class NullInterceptor implements Interceptor<Message> {

        @Override
        public void handleMessage(Message message) throws Fault {

        }

        @Override
        public void handleFault(Message message) {

        }
    }

    @Rule
    public BQTestFactory testFactory = new BQTestFactory().autoLoadModules();

    @Test
    public void testNoEndpointInterceptors() {
        BQRuntime runtime = testFactory.app()
                .module(binder -> {
                    CxfJaxwsServerModule.extend(binder).addEndpoint(() -> Endpoint.publish("/test", new HelloWorldImpl()));
                })
                .createRuntime();


        Set<Endpoint> endpoints = getEndpoints(runtime);

        endpoints.forEach(endpoint -> {
            EndpointImpl cxfEndpoint = (EndpointImpl) endpoint;

            Assert.assertTrue(cxfEndpoint.getInInterceptors().isEmpty());
            Assert.assertTrue(cxfEndpoint.getOutInterceptors().isEmpty());
            Assert.assertTrue(cxfEndpoint.getInFaultInterceptors().isEmpty());
            Assert.assertTrue(cxfEndpoint.getOutFaultInterceptors().isEmpty());
        });
    }

    private Set<Endpoint> getEndpoints(BQRuntime runtime) {
        return runtime.getInstance(Key.get(new TypeLiteral<Set<Endpoint>>() {}));
    }


    @Test
    public void testEndpointInterceptors() {


        NullInterceptor in = new NullInterceptor();
        NullInterceptor inFault = new NullInterceptor();
        NullInterceptor out = new NullInterceptor();
        NullInterceptor outFault = new NullInterceptor();

        NullInterceptor in_1 = new NullInterceptor();
        NullInterceptor inFault_1 = new NullInterceptor();
        NullInterceptor out_1 = new NullInterceptor();
        NullInterceptor outFault_1 = new NullInterceptor();

        List<Interceptor<Message>> endpointInterceptors = asList(in, out, inFault, out, outFault, in_1, out_1, inFault_1, outFault_1);


        BQRuntime runtime = testFactory.app().module(binder -> {
            CxfJaxwsServerModule.extend(binder)
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
                    .addOutFaultInterceptor(outFault);

        }).createRuntime();
        Bus bus = runtime.getInstance(Bus.class);

        // check that server endpoints are not leaking to bus
        Assert.assertTrue(Collections.disjoint(bus.getInFaultInterceptors(), endpointInterceptors));
        Assert.assertTrue(Collections.disjoint(bus.getInInterceptors(), endpointInterceptors));
        Assert.assertTrue(Collections.disjoint(bus.getOutFaultInterceptors(), endpointInterceptors));
        Assert.assertTrue(Collections.disjoint(bus.getOutInterceptors(), endpointInterceptors));

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
        Assert.assertArrayEquals(endpoint.getInInterceptors().toArray(), in.toArray());
        Assert.assertArrayEquals(endpoint.getInFaultInterceptors().toArray(), inFault.toArray());
        Assert.assertArrayEquals(endpoint.getOutInterceptors().toArray(), out.toArray());
        Assert.assertArrayEquals(endpoint.getOutFaultInterceptors().toArray(), outFault.toArray());

    }
}
