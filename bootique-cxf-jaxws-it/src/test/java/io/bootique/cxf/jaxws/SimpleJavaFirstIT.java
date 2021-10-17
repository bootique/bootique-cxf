package io.bootique.cxf.jaxws;

import io.bootique.cxf.CxfModule;
import io.bootique.junit5.BQTest;
import io.bootique.junit5.BQTestFactory;
import io.bootique.junit5.BQTestTool;
import org.apache.cxf.ext.logging.LoggingFeature;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.junit.jupiter.api.Test;

import javax.xml.ws.Endpoint;
import static org.junit.jupiter.api.Assertions.*;

@BQTest
public class SimpleJavaFirstIT {

    @BQTestTool
    static final BQTestFactory TEST_FACTORY = new BQTestFactory().autoLoadModules();

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

        assertEquals(expectedResponse, responseFromClient);
    }
}
