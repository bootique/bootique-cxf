package io.bootique.cxf.jaxws;

import com.google.inject.Binder;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import io.bootique.ConfigModule;
import io.bootique.config.ConfigurationFactory;
import io.bootique.cxf.CxfModule;
import io.bootique.cxf.CxfModuleExtender;
import io.bootique.jetty.JettyModule;
import io.bootique.jetty.MappedServlet;
import org.apache.cxf.Bus;
import org.apache.cxf.feature.Feature;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.message.Message;
import org.apache.cxf.transport.servlet.AbstractHTTPServlet;

import javax.xml.ws.Endpoint;
import java.util.Set;

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
        CxfModule.extend(binder).addCustomConfigurer(EndpointImpl.class, EndpointConfigurer.class);


        final TypeLiteral<MappedServlet<AbstractHTTPServlet>> servletTypeLiteral = new TypeLiteral<MappedServlet<AbstractHTTPServlet>>() {};

        JettyModule.extend(binder).addMappedServlet(servletTypeLiteral);
    }

    @Provides
    @Singleton
    public MappedServlet<AbstractHTTPServlet> provideServlet(Set<Endpoint> endpoints, Bus bus, ConfigurationFactory configurationFactory) {

        // TODO the sole purpose of endpoints here is to add them to the Guice dependency graph. Need a better way to achieve that.

        return configurationFactory
                .config(CxfJaxwsServletFactory.class, configPrefix)
                .createCxfServlet(bus);

    }

    @Provides
    @Singleton
    public EndpointConfigurer provideEndpointConfigurer(
            Set<Feature> features,
            @Named(CxfModuleExtender.IN_DIRECTION) Set<Interceptor<? extends Message>> inInterceptors,
            @Named(CxfModuleExtender.OUT_DIRECTION) Set<Interceptor<? extends Message>> outInterceptors,
            @Named(CxfModuleExtender.IN_FAULT_DIRECTION) Set<Interceptor<? extends Message>> inFaultInterceptors,
            @Named(CxfModuleExtender.OUT_FAULT_DIRECTION) Set<Interceptor<? extends Message>> outFaultInterceptors
    ) {


        return new EndpointConfigurer(
                features,
                inInterceptors,
                outInterceptors,
                inFaultInterceptors,
                outFaultInterceptors
                );
    }



}
