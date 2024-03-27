package io.bootique.cxf.jaxws;

import io.bootique.BQRuntime;
import io.bootique.Bootique;
import io.bootique.cxf.CxfModule;
import io.bootique.junit5.BQApp;
import io.bootique.junit5.BQTest;
import org.apache.cxf.ext.logging.LoggingFeature;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.junit.jupiter.api.Test;

import javax.xml.ws.Endpoint;

import static org.junit.jupiter.api.Assertions.assertEquals;

@BQTest
public class SimpleJavaFirstIT {

    @BQApp
    static final BQRuntime app = Bootique.app("-s")
            .autoLoadModules()
            .module(b -> {

                CxfModule.extend(b).addFeature(LoggingFeature.class);

                CxfJaxwsServerModule
                        .extend(b)
                        .addEndpoint(() -> Endpoint.publish("/test", new HelloWorldImpl()));
            })
            .createRuntime();

    @Test
    public void simpleService() {

        JaxWsProxyFactoryBean proxyFactoryBean = new JaxWsProxyFactoryBean();
        proxyFactoryBean.setAddress("http://localhost:8080/test");
        HelloWorld helloWorldClient = proxyFactoryBean.create(HelloWorld.class);

        String responseFromClient = helloWorldClient.sayHi("Simple Client");
        assertEquals("Hello Simple Client", responseFromClient);
    }
}
