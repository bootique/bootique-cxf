package io.bootique.cxf.jaxws;

import io.bootique.BQRuntime;
import io.bootique.Bootique;
import io.bootique.cxf.CxfModule;
import io.bootique.jetty.junit.JettyTester;
import io.bootique.junit.BQApp;
import io.bootique.junit.BQTest;
import jakarta.xml.ws.Endpoint;
import org.apache.cxf.ext.logging.LoggingFeature;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@BQTest
public class SimpleJavaFirstIT {

    static final JettyTester jetty = JettyTester.create();

    @BQApp
    static final BQRuntime app = Bootique.app("-s")
            .autoLoadModules()
            .module(jetty.moduleReplacingConnectors())
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
        proxyFactoryBean.setAddress(jetty.getUrl() + "/test");
        HelloWorld helloWorldClient = proxyFactoryBean.create(HelloWorld.class);

        String responseFromClient = helloWorldClient.sayHi("Simple Client");
        assertEquals("Hello Simple Client", responseFromClient);
    }
}
