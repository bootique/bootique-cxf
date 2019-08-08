package io.bootique.cxf.jaxws;

import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import io.bootique.ConfigModule;
import io.bootique.config.ConfigurationFactory;
import io.bootique.cxf.CxfModule;
import io.bootique.cxf.interceptor.CxfInterceptors;
import io.bootique.jetty.JettyModule;
import io.bootique.jetty.MappedServlet;
import org.apache.cxf.Bus;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.message.Message;
import org.apache.cxf.transport.servlet.AbstractHTTPServlet;

import javax.xml.ws.Endpoint;
import java.util.Set;

import static io.bootique.cxf.jaxws.CxfJaxwsServerModuleExtender.JAX_WS_SERVER_INTERCEPTORS;

public class CxfJaxwsServerModule extends ConfigModule {

    public static final String CONF_PREFIX = "cxfjaxwsserver";

    public static CxfJaxwsServerModuleExtender extend(Binder binder) {
        return new CxfJaxwsServerModuleExtender(binder);
    }

    public CxfJaxwsServerModule() {
        super(CONF_PREFIX);
    }

    @Override
    public void configure(Binder binder) {

        CxfJaxwsServerModule.extend(binder).initAllExtensions();
        CxfModule.extend(binder).addCustomConfigurer(EndpointImpl.class, EndpointConfigurer.class, true);


        final TypeLiteral<MappedServlet<AbstractHTTPServlet>> servletTypeLiteral = new TypeLiteral<MappedServlet<AbstractHTTPServlet>>() {};

        JettyModule.extend(binder).addMappedServlet(servletTypeLiteral);
    }

    @Provides
    @Singleton
    public MappedServlet<AbstractHTTPServlet> provideServlet(Set<Endpoint> endpoints, Bus bus, ConfigurationFactory configFactory) {

        // TODO the sole purpose of endpoints here is to add them to the Guice dependency graph. Need a better way to achieve that.

        return config(CxfJaxwsServletFactory.class, configFactory).createCxfServlet(bus);
    }

    @Provides
    @Singleton
    public EndpointConfigurer provideEndpointConfigurer(
            @CxfInterceptors(target = JAX_WS_SERVER_INTERCEPTORS, type = CxfInterceptors.Type.IN) Set<Interceptor<? extends Message>> inInterceptors,
            @CxfInterceptors(target = JAX_WS_SERVER_INTERCEPTORS, type = CxfInterceptors.Type.OUT) Set<Interceptor<? extends Message>> outInterceptors,
            @CxfInterceptors(target = JAX_WS_SERVER_INTERCEPTORS, type = CxfInterceptors.Type.IN_FAULT) Set<Interceptor<? extends Message>> inFaultInterceptors,
            @CxfInterceptors(target = JAX_WS_SERVER_INTERCEPTORS, type = CxfInterceptors.Type.OUT_FAULT) Set<Interceptor<? extends Message>> outFaultInterceptors,
            Injector injector
    ) {


        return new EndpointConfigurer(
                inInterceptors,
                outInterceptors,
                inFaultInterceptors,
                outFaultInterceptors,
                injector
                );
    }



}
