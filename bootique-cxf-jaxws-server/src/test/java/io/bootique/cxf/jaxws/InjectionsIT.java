package io.bootique.cxf.jaxws;

import com.google.inject.Inject;
import io.bootique.BQRuntime;
import io.bootique.test.junit.BQTestFactory;
import org.apache.cxf.jaxws.EndpointImpl;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.xml.ws.Endpoint;
import javax.xml.ws.WebServiceContext;

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

    public static class Injectee {};

    @Rule
    public BQTestFactory testFactory = new BQTestFactory().autoLoadModules();

    @Test
    public void testNewInstanceInjections() {
        Injectee injectee = new Injectee();

        testFactory.app().module(binder -> {

            binder.bind(Injectee.class).toInstance(injectee);
        }).createRuntime();

        EndpointImpl endpoint = (EndpointImpl) Endpoint.publish("/test", new HelloWorldWithInjection());
        HelloWorldWithInjection implementor = (HelloWorldWithInjection) endpoint.getImplementor();

        Assert.assertEquals(injectee, implementor.injectee);
        Assert.assertNotNull(implementor.context);

    }

    @Test
    public void testProvidedInjections() {
        Injectee injectee = new Injectee();

        BQRuntime runtime = testFactory.app().module(binder -> {

            binder.bind(Injectee.class).toInstance(injectee);
            binder.bind(HelloWorld.class).to(HelloWorldWithInjection.class);
        }).createRuntime();

        HelloWorld providedImplementor = runtime.getInstance(HelloWorld.class);
        EndpointImpl endpoint = (EndpointImpl) Endpoint.publish("/test", providedImplementor);
        HelloWorldWithInjection implementor = (HelloWorldWithInjection) endpoint.getImplementor();

        Assert.assertEquals(injectee, implementor.injectee);
        Assert.assertNotNull(implementor.context);
    }
}
