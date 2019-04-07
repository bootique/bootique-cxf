package io.bootique.cxf.jaxws;

import io.bootique.cxf.CxfModule;
import io.bootique.test.junit.BQTestFactory;
import org.apache.cxf.ext.logging.LoggingFeature;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

import javax.xml.ws.Endpoint;

public class SimpleJavaFirstIT {

    @ClassRule
    public static BQTestFactory TEST_FACTORY = new BQTestFactory().autoLoadModules();


    @Test
    public void testSimpleService() {

        HelloWorldImpl serviceImpl = new HelloWorldImpl();
        TEST_FACTORY.app("-s")
                .module(binder -> {
                    // adding logging for both client and a server
                    CxfModule.extend(binder).addFeature(LoggingFeature.class);
                    CxfJaxwsServerModule.extend(binder)
                            .addEndpoint(() -> Endpoint.publish("/test", serviceImpl));
                })
                .run();

        JaxWsProxyFactoryBean proxyFactoryBean = new JaxWsProxyFactoryBean();
        proxyFactoryBean.setAddress("http://localhost:8080/test");
        HelloWorld helloWorldClient = proxyFactoryBean.create(HelloWorld.class);

        String responseFromClient = helloWorldClient.sayHi("Simple Client");
        String expectedResponse = serviceImpl.sayHi("Simple Client");

        Assert.assertEquals(expectedResponse, responseFromClient);

    }
}
