package io.bootique.cxf.jaxws;

import io.bootique.BQRuntime;
import io.bootique.junit5.BQTest;
import io.bootique.junit5.BQTestFactory;
import io.bootique.junit5.BQTestTool;
import org.apache.cxf.Bus;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.message.Message;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static java.util.Arrays.asList;

@BQTest
public class JaxWsProxyFactoryInterceptorsIT {

    static class NullInterceptor implements Interceptor<Message> {

        @Override
        public void handleMessage(Message message) throws Fault {

        }

        @Override
        public void handleFault(Message message) {

        }
    }

    @BQTestTool
    final BQTestFactory testFactory = new BQTestFactory().autoLoadModules();

    @Test
    public void noClientInterceptors() {
        testFactory.app().createRuntime();

        JaxWsProxyFactoryBean proxyFactoryBean = new JaxWsProxyFactoryBean();
        HelloWorld client = proxyFactoryBean.create(HelloWorld.class);

        assertTrue(proxyFactoryBean.getInInterceptors().isEmpty());
        assertTrue(proxyFactoryBean.getOutInterceptors().isEmpty());
        assertTrue(proxyFactoryBean.getInFaultInterceptors().isEmpty());
        assertTrue(proxyFactoryBean.getOutFaultInterceptors().isEmpty());
    }


    @Test
    public void clientInterceptors() {

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
            CxfJaxwsClientModule.extend(binder)
                    .contributeClientInterceptors()
                    .addInInterceptor(in)
                    .addOutInterceptor(out)
                    .addInFaultInterceptor(inFault)
                    .addOutFaultInterceptor(outFault);

        }).createRuntime();
        Bus bus = runtime.getInstance(Bus.class);


        JaxWsProxyFactoryBean proxyFactoryBean = new JaxWsProxyFactoryBean();
        proxyFactoryBean.getInInterceptors().add(in_1);
        proxyFactoryBean.getInFaultInterceptors().add(inFault_1);
        proxyFactoryBean.getOutInterceptors().add(out_1);
        proxyFactoryBean.getOutFaultInterceptors().add(outFault_1);
        HelloWorld client = proxyFactoryBean.create(HelloWorld.class);

        JaxWsProxyFactoryBean proxyFactoryBean2 = new JaxWsProxyFactoryBean();
        HelloWorld client2 = proxyFactoryBean2.create(HelloWorld.class);


        // check that server endpoints are not leaking to bus
        assertTrue(Collections.disjoint(bus.getInFaultInterceptors(), endpointInterceptors));
        assertTrue(Collections.disjoint(bus.getInInterceptors(), endpointInterceptors));
        assertTrue(Collections.disjoint(bus.getOutFaultInterceptors(), endpointInterceptors));
        assertTrue(Collections.disjoint(bus.getOutInterceptors(), endpointInterceptors));

        assertClientConfiguration(proxyFactoryBean, asList(in_1, in), asList(out_1, out), asList(inFault_1, inFault), asList(outFault_1, outFault));
        assertClientConfiguration(proxyFactoryBean2, asList(in), asList(out), asList(inFault), asList(outFault));


    }

    private <T extends Interceptor> void assertClientConfiguration(
            JaxWsProxyFactoryBean factoryBean,
            Collection<T> in,
            Collection<T> out,
            Collection<T> inFault,
            Collection<T> outFault) {
        assertArrayEquals(factoryBean.getInInterceptors().toArray(), in.toArray());
        assertArrayEquals(factoryBean.getInFaultInterceptors().toArray(), inFault.toArray());
        assertArrayEquals(factoryBean.getOutInterceptors().toArray(), out.toArray());
        assertArrayEquals(factoryBean.getOutFaultInterceptors().toArray(), outFault.toArray());
    }
}
