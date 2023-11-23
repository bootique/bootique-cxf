package io.bootique.cxf;

import io.bootique.BQRuntime;
import io.bootique.junit5.BQTest;
import io.bootique.junit5.BQTestFactory;
import io.bootique.junit5.BQTestTool;
import org.apache.cxf.Bus;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.message.Message;
import org.junit.jupiter.api.Test;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@BQTest
public class InterceptorsIT {

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
    public void noBusInterceptors() {
        BQRuntime runtime = testFactory.app().createRuntime();
        Bus bus = runtime.getInstance(Bus.class);

        assertTrue(bus.getInInterceptors().isEmpty());
        assertTrue(bus.getOutInterceptors().isEmpty());
        assertTrue(bus.getInFaultInterceptors().isEmpty());
        assertTrue(bus.getOutFaultInterceptors().isEmpty());
    }

    @Test
    public void busInterceptors() {


        NullInterceptor in = new NullInterceptor();
        NullInterceptor inFault = new NullInterceptor();
        NullInterceptor out = new NullInterceptor();
        NullInterceptor outFault = new NullInterceptor();


        BQRuntime runtime = testFactory.app().module(b ->
                CxfModule.extend(b)
                        .contributeBusInterceptors()
                        .addInInterceptor(in)
                        .addInFaultInterceptor(inFault)
                        .addOutInterceptor(out)
                        .addOutFaultInterceptor(outFault)).createRuntime();
        Bus bus = runtime.getInstance(Bus.class);

        assertArrayEquals(bus.getInInterceptors().toArray(), singletonList(in).toArray());
        assertArrayEquals(bus.getInFaultInterceptors().toArray(), singletonList(inFault).toArray());
        assertArrayEquals(bus.getOutInterceptors().toArray(), singletonList(out).toArray());
        assertArrayEquals(bus.getOutFaultInterceptors().toArray(), singletonList(outFault).toArray());

    }

}
