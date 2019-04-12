package io.bootique.cxf.jaxws;

import io.bootique.BQRuntime;
import io.bootique.test.junit.BQTestFactory;
import org.apache.cxf.Bus;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.message.Message;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;

public class JaxWsProxyFactoryInterceptorsIT {

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
    public void testNoClientInterceptors() {
        BQRuntime runtime = testFactory.app()
                .createRuntime();


        JaxWsProxyFactoryBean proxyFactoryBean = new JaxWsProxyFactoryBean();
        HelloWorld client = proxyFactoryBean.create(HelloWorld.class);


        Assert.assertTrue(proxyFactoryBean.getInInterceptors().isEmpty());
        Assert.assertTrue(proxyFactoryBean.getOutInterceptors().isEmpty());
        Assert.assertTrue(proxyFactoryBean.getInFaultInterceptors().isEmpty());
        Assert.assertTrue(proxyFactoryBean.getOutFaultInterceptors().isEmpty());
    }


    @Test
    public void testClientInterceptors() {


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
        Assert.assertTrue(Collections.disjoint(bus.getInFaultInterceptors(), endpointInterceptors));
        Assert.assertTrue(Collections.disjoint(bus.getInInterceptors(), endpointInterceptors));
        Assert.assertTrue(Collections.disjoint(bus.getOutFaultInterceptors(), endpointInterceptors));
        Assert.assertTrue(Collections.disjoint(bus.getOutInterceptors(), endpointInterceptors));

        assertClientConfiguration(proxyFactoryBean, asList(in_1, in), asList(out_1, out), asList(inFault_1, inFault), asList(outFault_1, outFault));
        assertClientConfiguration(proxyFactoryBean2, asList(in), asList(out), asList(inFault), asList(outFault));


    }

    private <T extends Interceptor> void assertClientConfiguration(
            JaxWsProxyFactoryBean factoryBean,
            Collection<T> in,
            Collection<T> out,
            Collection<T> inFault,
            Collection<T> outFault

    ) {
        Assert.assertArrayEquals(factoryBean.getInInterceptors().toArray(), in.toArray());
        Assert.assertArrayEquals(factoryBean.getInFaultInterceptors().toArray(), inFault.toArray());
        Assert.assertArrayEquals(factoryBean.getOutInterceptors().toArray(), out.toArray());
        Assert.assertArrayEquals(factoryBean.getOutFaultInterceptors().toArray(), outFault.toArray());

    }
}
