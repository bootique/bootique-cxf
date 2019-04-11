package io.bootique.cxf.jaxws;

import com.google.inject.Injector;
import org.apache.cxf.Bus;
import org.apache.cxf.transport.servlet.CXFNonSpringServlet;

import javax.servlet.ServletConfig;

public class CxfGuiceServlet extends CXFNonSpringServlet {



    public CxfGuiceServlet(Bus bus) {

        setBus(bus);
    }

    @Override
    protected void loadBus(ServletConfig sc) {
        // do nothing
    }
}
