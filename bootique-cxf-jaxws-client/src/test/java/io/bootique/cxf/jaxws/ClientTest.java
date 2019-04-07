package io.bootique.cxf.jaxws;

import io.bootique.test.junit.BQTestFactory;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;

public class ClientTest {

    @ClassRule
    public static BQTestFactory TEST_FACTORY = new BQTestFactory().autoLoadModules();


    @Test
    @Ignore
    public void testClient() {

        TEST_FACTORY.app().createRuntime();

        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setAddress("http://localhost:9000/helloWorld");
        HelloWorld client = factory.create(HelloWorld.class);
        System.out.println(client.sayHi("World"));

        client.sayHi("Hi");
    }
}
