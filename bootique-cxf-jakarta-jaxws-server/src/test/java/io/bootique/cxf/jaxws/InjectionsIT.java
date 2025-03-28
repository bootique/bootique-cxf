package io.bootique.cxf.jaxws;

import io.bootique.BQRuntime;
import io.bootique.junit5.BQTest;
import io.bootique.junit5.BQTestFactory;
import io.bootique.junit5.BQTestTool;
import jakarta.annotation.Resource;
import jakarta.jws.WebService;
import jakarta.xml.ws.Endpoint;
import jakarta.xml.ws.WebServiceContext;
import org.apache.cxf.jaxws.EndpointImpl;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@BQTest
public class InjectionsIT {

    @WebService
    public static class HelloWorldWithInjection implements HelloWorld {

        @Inject
        private Injectee injectee;

        @Resource
        private WebServiceContext context;

        @Override
        public String sayHi(String text) {
            return "Hi";
        }

    }

    public static class Injectee {
    }

    @BQTestTool
    final BQTestFactory testFactory = new BQTestFactory().autoLoadModules();

    @Test
    public void newInstanceInjections() {
        Injectee injectee = new Injectee();

        testFactory.app().module(binder -> {

            binder.bind(Injectee.class).toInstance(injectee);
        }).createRuntime();

        EndpointImpl endpoint = (EndpointImpl) Endpoint.publish("/test", new HelloWorldWithInjection());
        HelloWorldWithInjection implementor = (HelloWorldWithInjection) endpoint.getImplementor();

        assertEquals(injectee, implementor.injectee);
        assertNotNull(implementor.context);
    }

    @Test
    public void providedInjections() {
        Injectee injectee = new Injectee();

        BQRuntime runtime = testFactory.app().module(binder -> {

            binder.bind(Injectee.class).toInstance(injectee);
            binder.bind(HelloWorld.class).to(HelloWorldWithInjection.class);
        }).createRuntime();

        HelloWorld providedImplementor = runtime.getInstance(HelloWorld.class);
        EndpointImpl endpoint = (EndpointImpl) Endpoint.publish("/test", providedImplementor);
        HelloWorldWithInjection implementor = (HelloWorldWithInjection) endpoint.getImplementor();

        assertEquals(injectee, implementor.injectee);
        assertNotNull(implementor.context);
    }
}
