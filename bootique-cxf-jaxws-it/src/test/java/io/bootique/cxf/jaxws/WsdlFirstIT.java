package io.bootique.cxf.jaxws;


import com.example.customerservice.Customer;
import com.example.customerservice.CustomerService;
import com.example.customerservice.CustomerServiceService;
import com.example.customerservice.NoSuchCustomerException;
import io.bootique.cxf.CxfModule;
import io.bootique.test.junit.BQTestFactory;
import org.apache.cxf.ext.logging.LoggingFeature;
import org.apache.cxf.jaxws.EndpointImpl;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import java.net.URL;
import java.util.Date;

import static io.bootique.cxf.jaxws.CustomerServiceImpl.TEST_CUSTOMER;

public class WsdlFirstIT {

    @ClassRule
    public static BQTestFactory TEST_FACTORY = new BQTestFactory().autoLoadModules();
    private static CustomerService CLIENT;

    @BeforeClass
    public static void setUp() throws Exception {
        TEST_FACTORY.app("-s")
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
                .run();

        String wsdlLoc = "http://localhost:8080/test?wsdl";

        CustomerServiceService service = new CustomerServiceService(new URL(wsdlLoc));
        CLIENT = service.getCustomerServicePort();
    }

    @Test
    public void testUpdateCustomer() throws Exception {

        Customer customer = new Customer();
        customer.setBirthDate(new Date());
        customer.setCustomerId(123);
        CLIENT.updateCustomer(customer);
    }

    @Test
    public void testExistingCustomer() throws NoSuchCustomerException {

        Customer testCustomer = CLIENT.getCustomersByName("test").get(0);


        Assert.assertEquals(TEST_CUSTOMER.getAddress(), testCustomer.getAddress());
        Assert.assertEquals(TEST_CUSTOMER.getBirthDate(), testCustomer.getBirthDate());
        Assert.assertEquals(TEST_CUSTOMER.getCustomerId(), testCustomer.getCustomerId());
        Assert.assertEquals(TEST_CUSTOMER.getName(), testCustomer.getName());
        Assert.assertEquals(TEST_CUSTOMER.getNumOrders(), testCustomer.getNumOrders());
        Assert.assertEquals(TEST_CUSTOMER.getType(), testCustomer.getType());

    }

    @Test
    public void testNonExistingCustomer() throws NoSuchCustomerException {

        boolean thrown = false;
        try {
            CLIENT.getCustomersByName("noexists");
        } catch (NoSuchCustomerException e) {
            thrown = true;
            Assert.assertEquals("noexists", e.getFaultInfo().getCustomerName());
        }

        Assert.assertTrue(thrown);

    }
}
