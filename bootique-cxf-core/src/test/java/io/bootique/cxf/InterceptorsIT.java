package io.bootique.cxf;

import io.bootique.BQRuntime;
import io.bootique.test.junit.BQTestFactory;
import org.apache.cxf.Bus;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.message.Message;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import static java.util.Collections.singletonList;

public class InterceptorsIT {

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
    public void testNoBusInterceptors() {
        BQRuntime runtime = testFactory.app().createRuntime();
        Bus bus = runtime.getInstance(Bus.class);

        Assert.assertTrue(bus.getInInterceptors().isEmpty());
        Assert.assertTrue(bus.getOutInterceptors().isEmpty());
        Assert.assertTrue(bus.getInFaultInterceptors().isEmpty());
        Assert.assertTrue(bus.getOutFaultInterceptors().isEmpty());
    }


    @Test
    public void testBusInterceptors() {


        NullInterceptor in = new NullInterceptor();
        NullInterceptor inFault = new NullInterceptor();
        NullInterceptor out = new NullInterceptor();
        NullInterceptor outFault = new NullInterceptor();


        BQRuntime runtime = testFactory.app().module(binder -> {
            CxfModule.extend(binder)
                    .contributeBusInterceptors()
                    .addInInterceptor(in)
                    .addInFaultInterceptor(inFault)
                    .addOutInterceptor(out)
                    .addOutFaultInterceptor(outFault);

        }).createRuntime();
        Bus bus = runtime.getInstance(Bus.class);

        Assert.assertArrayEquals(bus.getInInterceptors().toArray(), singletonList(in).toArray());
        Assert.assertArrayEquals(bus.getInFaultInterceptors().toArray(), singletonList(inFault).toArray());
        Assert.assertArrayEquals(bus.getOutInterceptors().toArray(), singletonList(out).toArray());
        Assert.assertArrayEquals(bus.getOutFaultInterceptors().toArray(), singletonList(outFault).toArray());

    }

}
