package io.bootique.cxf.jaxws;

import io.bootique.BQModuleProvider;
import io.bootique.ModuleCrate;
import io.bootique.config.ConfigurationFactory;
import io.bootique.cxf.CxfModule;
import io.bootique.cxf.jaxws.annotation.CxfInterceptorsServerIn;
import io.bootique.cxf.jaxws.annotation.CxfInterceptorsServerInFault;
import io.bootique.cxf.jaxws.annotation.CxfInterceptorsServerOut;
import io.bootique.cxf.jaxws.annotation.CxfInterceptorsServerOutFault;
import io.bootique.di.*;
import io.bootique.jetty.JettyModule;
import io.bootique.jetty.JettyModuleProvider;
import io.bootique.jetty.MappedServlet;
import org.apache.cxf.Bus;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.message.Message;
import org.apache.cxf.transport.servlet.AbstractHTTPServlet;

import javax.inject.Singleton;
import javax.xml.ws.Endpoint;
import java.util.Collection;
import java.util.Set;

import static java.util.Arrays.asList;

public class CxfJaxwsServerModule implements BQModule, BQModuleProvider {

    private static final String CONFIG_PREFIX = "cxfjaxwsserver";

    public static CxfJaxwsServerModuleExtender extend(Binder binder) {
        return new CxfJaxwsServerModuleExtender(binder);
    }

    @Override
    public ModuleCrate moduleCrate() {
        return ModuleCrate.of(this)
                .provider(this)
                .description("Integrates Apache CXF JAX-WS server engine")
                .config(CONFIG_PREFIX, CxfJaxwsServletFactory.class)
                .build();
    }

    @Override
    @Deprecated(since = "3.0", forRemoval = true)
    public Collection<BQModuleProvider> dependencies() {
        return asList(
                new JettyModuleProvider(),
                new CxfModule()
        );
    }

    @Override
    public void configure(Binder binder) {

        CxfJaxwsServerModule.extend(binder).initAllExtensions();
        CxfModule.extend(binder).addCustomConfigurer(EndpointImpl.class, EndpointConfigurer.class, true);

        JettyModule.extend(binder).addMappedServlet(new TypeLiteral<MappedServlet<AbstractHTTPServlet>>() {});
    }

    @Provides
    @Singleton
    public MappedServlet<AbstractHTTPServlet> provideServlet(Set<Endpoint> endpoints, Bus bus, ConfigurationFactory configFactory) {

        // TODO the sole purpose of endpoints here is to add them to the BQ dependency graph. Need a better way to achieve that.
        return configFactory.config(CxfJaxwsServletFactory.class, CONFIG_PREFIX).createCxfServlet(bus);
    }

    @Provides
    @Singleton
    public EndpointConfigurer provideEndpointConfigurer(
            @CxfInterceptorsServerIn        Set<Interceptor<? extends Message>> inInterceptors,
            @CxfInterceptorsServerOut       Set<Interceptor<? extends Message>> outInterceptors,
            @CxfInterceptorsServerInFault   Set<Interceptor<? extends Message>> inFaultInterceptors,
            @CxfInterceptorsServerOutFault  Set<Interceptor<? extends Message>> outFaultInterceptors,
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
