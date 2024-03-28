package io.bootique.cxf.jaxws;


import com.example.customerservice.Customer;
import com.example.customerservice.CustomerService;
import com.example.customerservice.CustomerServiceService;
import com.example.customerservice.NoSuchCustomerException;
import io.bootique.BQRuntime;
import io.bootique.Bootique;
import io.bootique.cxf.CxfModule;
import io.bootique.jetty.junit5.JettyTester;
import io.bootique.junit5.BQApp;
import io.bootique.junit5.BQTest;
import org.apache.cxf.ext.logging.LoggingFeature;
import org.apache.cxf.jaxws.EndpointImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.util.Date;

import static io.bootique.cxf.jaxws.CustomerServiceImpl.TEST_CUSTOMER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@BQTest
public class WsdlFirstIT {

    static final JettyTester jetty = JettyTester.create();

    @BQApp
    static final BQRuntime app = Bootique.app("-s")
            .autoLoadModules()
            .module(jetty.moduleReplacingConnectors())
            .module(binder -> {
                // adding logging for both client and a server
                CxfModule.extend(binder).addFeature(LoggingFeature.class);
                CxfJaxwsServerModule.extend(binder)
                        .addEndpoint(() -> {
                            EndpointImpl endpoint = new EndpointImpl(new CustomerServiceImpl());
                            endpoint.setWsdlLocation("classpath:CustomerService.wsdl");
                            endpoint.publish("/test");
                            return endpoint;
                        });
            })
            .createRuntime();

    private static CustomerService CLIENT;

    @BeforeAll
    public static void setUp() throws Exception {
        String wsdlLoc = jetty.getUrl() + "/test?wsdl";
        CustomerServiceService service = new CustomerServiceService(new URL(wsdlLoc));
        CLIENT = service.getCustomerServicePort();
    }

    @Test
    public void updateCustomer() {

        Customer customer = new Customer();
        customer.setBirthDate(new Date());
        customer.setCustomerId(123);
        CLIENT.updateCustomer(customer);
    }

    @Test
    public void existingCustomer() throws NoSuchCustomerException {

        Customer testCustomer = CLIENT.getCustomersByName("test").get(0);

        assertEquals(TEST_CUSTOMER.getAddress(), testCustomer.getAddress());
        assertEquals(TEST_CUSTOMER.getBirthDate(), testCustomer.getBirthDate());
        assertEquals(TEST_CUSTOMER.getCustomerId(), testCustomer.getCustomerId());
        assertEquals(TEST_CUSTOMER.getName(), testCustomer.getName());
        assertEquals(TEST_CUSTOMER.getNumOrders(), testCustomer.getNumOrders());
        assertEquals(TEST_CUSTOMER.getType(), testCustomer.getType());
    }

    @Test
    public void nonExistingCustomer() {

        boolean thrown = false;
        try {
            CLIENT.getCustomersByName("noexists");
        } catch (NoSuchCustomerException e) {
            thrown = true;
            assertEquals("noexists", e.getFaultInfo().getCustomerName());
        }

        assertTrue(thrown);
    }
}
