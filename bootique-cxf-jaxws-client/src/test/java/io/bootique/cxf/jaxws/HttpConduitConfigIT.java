package io.bootique.cxf.jaxws;

import io.bootique.BQRuntime;
import io.bootique.cxf.CxfModule;
import io.bootique.cxf.conf.CustomConfigurer;
import io.bootique.test.junit.BQTestFactory;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.URLConnectionHTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

public class HttpConduitConfigIT {


    @Rule
    public BQTestFactory testFactory = new BQTestFactory().autoLoadModules();


    @Test
    public void noAdditionalConfiguration() {
        BQRuntime runtime = testFactory.app().createRuntime();

        JaxWsProxyFactoryBean factoryBean = new JaxWsProxyFactoryBean();
        HelloWorld helloWorld = factoryBean.create(HelloWorld.class);

        URLConnectionHTTPConduit conduit = (URLConnectionHTTPConduit) ClientProxy.getClient(helloWorld).getConduit();


        // defaults
        HTTPClientPolicy clientPolicy = conduit.getClient();
        Assert.assertFalse(clientPolicy.isAutoRedirect());
        Assert.assertEquals(30 * 1000, clientPolicy.getConnectionTimeout());
        Assert.assertEquals(60 * 1000, clientPolicy.getReceiveTimeout());
    }

    @Test
    public void configurationFromFile() {
        BQRuntime runtime = testFactory.app("--config=classpath:http-config.yaml").createRuntime();

        JaxWsProxyFactoryBean factoryBean = new JaxWsProxyFactoryBean();
        HelloWorld helloWorld = factoryBean.create(HelloWorld.class);

        URLConnectionHTTPConduit conduit = (URLConnectionHTTPConduit) ClientProxy.getClient(helloWorld).getConduit();


        // Config from file
        //        cxfjaxwsclient:
        //          followRedirects: true
        //          readTimeoutMs: 25000
        //          connectTimeoutMs: 35000

        HTTPClientPolicy clientPolicy = conduit.getClient();
        Assert.assertTrue(clientPolicy.isAutoRedirect());
        Assert.assertEquals(35 * 1000, clientPolicy.getConnectionTimeout());
        Assert.assertEquals(25 * 1000, clientPolicy.getReceiveTimeout());
    }


    public static class CustomHTTPConduitConfigurer implements CustomConfigurer<URLConnectionHTTPConduit> {

        static boolean LOADED = false;
        @Override
        public void configure(URLConnectionHTTPConduit instance) {
            LOADED = true;
        }
    }

    @Test
    public void addingConduitConfigurer() {


        testFactory.app()
                .module(binder -> {
                    CxfModule.extend(binder).addCustomConfigurer(URLConnectionHTTPConduit.class, CustomHTTPConduitConfigurer.class);
                })
                .createRuntime();

        JaxWsProxyFactoryBean factoryBean = new JaxWsProxyFactoryBean();
        HelloWorld helloWorld = factoryBean.create(HelloWorld.class);

        ClientProxy.getClient(helloWorld).getConduit();

        Assert.assertTrue(CustomHTTPConduitConfigurer.LOADED);
    }
}
