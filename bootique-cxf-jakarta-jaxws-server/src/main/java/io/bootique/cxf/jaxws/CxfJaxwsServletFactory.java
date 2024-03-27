package io.bootique.cxf.jaxws;

import io.bootique.annotation.BQConfig;
import io.bootique.annotation.BQConfigProperty;
import io.bootique.jetty.MappedServlet;
import org.apache.cxf.Bus;
import org.apache.cxf.transport.servlet.AbstractHTTPServlet;
import org.apache.cxf.transport.servlet.CXFNonSpringServlet;

import javax.inject.Inject;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

@BQConfig("Configures the servlet that is an entry point Apache CXF managed JAX-WS server")
public class CxfJaxwsServletFactory {

    private final Bus bus;

    private String urlPattern;

    @Inject
    public CxfJaxwsServletFactory(Bus bus) {
        this.bus = bus;
        this.urlPattern = "/*";
    }

    @BQConfigProperty("URL pattern for the servlet. Default is \"/*\"")
    public void setUrlPattern(String urlPattern) {
        this.urlPattern = urlPattern;
    }

    public MappedServlet<AbstractHTTPServlet> createServlet() {
        CXFNonSpringServlet servlet = new CXFNonSpringServlet();
        servlet.setBus(Objects.requireNonNull(bus));

        Set<String> urlPatterns = Collections.singleton(Objects.requireNonNull(urlPattern));
        return new MappedServlet<>(servlet, urlPatterns, "cxfjaxws");
    }

}
