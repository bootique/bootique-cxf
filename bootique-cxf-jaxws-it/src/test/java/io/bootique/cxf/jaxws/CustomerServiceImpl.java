package io.bootique.cxf.jaxws;

import com.example.customerservice.*;
import jakarta.annotation.Resource;
import jakarta.jws.WebService;
import jakarta.xml.ws.WebServiceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@WebService(endpointInterface = "com.example.customerservice.CustomerService")
public class CustomerServiceImpl implements CustomerService {

    private static Logger LOG = LoggerFactory.getLogger(CustomerServiceImpl.class);

    @Resource
    private WebServiceContext wsContext;
    protected static Customer TEST_CUSTOMER;

    static {
        TEST_CUSTOMER = new Customer();
        TEST_CUSTOMER.setCustomerId(1);
        TEST_CUSTOMER.setName("test");
        TEST_CUSTOMER.setType(CustomerType.BUSINESS);
        TEST_CUSTOMER.setNumOrders(2);
        TEST_CUSTOMER.setBirthDate(
                Date.from(
                        LocalDate.of(1988, Month.JUNE, 30)
                                .atStartOfDay()
                                .atZone(ZoneId.systemDefault())
                                .toInstant()
                )
        );
    }


    @Override
    public void updateCustomer(Customer customer) {

        LOG.info("Received update customer request. Principal is {}", wsContext.getUserPrincipal());

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            // Nothing to do here
        }
        LOG.info("Finished update.");
    }

    @Override
    public List<Customer> getCustomersByName(String name) throws NoSuchCustomerException {

        Objects.requireNonNull(name);

        if (name.equals("test")) {

            return Collections.singletonList(TEST_CUSTOMER);
        } else {
            NoSuchCustomer noSuchCustomer = new NoSuchCustomer();
            noSuchCustomer.setCustomerName(name);
            throw new NoSuchCustomerException("Couldnt find a customer" , noSuchCustomer);
        }

    }
}
