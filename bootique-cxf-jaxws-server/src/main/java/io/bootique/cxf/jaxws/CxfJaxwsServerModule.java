package io.bootique.cxf.jaxws;

import io.bootique.BQModule;
import io.bootique.ModuleCrate;
import io.bootique.config.ConfigurationFactory;
import io.bootique.cxf.CxfModule;
import io.bootique.cxf.jaxws.annotation.CxfInterceptorsServerIn;
import io.bootique.cxf.jaxws.annotation.CxfInterceptorsServerInFault;
import io.bootique.cxf.jaxws.annotation.CxfInterceptorsServerOut;
import io.bootique.cxf.jaxws.annotation.CxfInterceptorsServerOutFault;
import io.bootique.di.Binder;
import io.bootique.di.Injector;
import io.bootique.di.Provides;
import io.bootique.di.TypeLiteral;
import io.bootique.jetty.JettyModule;
import io.bootique.jetty.MappedServlet;
import jakarta.xml.ws.Endpoint;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.message.Message;
import org.apache.cxf.transport.servlet.AbstractHTTPServlet;

import jakarta.inject.Singleton;
import java.util.Set;

public class CxfJaxwsServerModule implements BQModule {

    private static final String CONFIG_PREFIX = "cxfjaxwsserver";

    public static CxfJaxwsServerModuleExtender extend(Binder binder) {
        return new CxfJaxwsServerModuleExtender(binder);
    }

    @Override
    public ModuleCrate crate() {
        return ModuleCrate.of(this)
                .description("Integrates Apache CXF JAX-WS server engine")
                .config(CONFIG_PREFIX, CxfJaxwsServletFactory.class)
                .build();
    }

    @Override
    public void configure(Binder binder) {

        CxfJaxwsServerModule.extend(binder).initAllExtensions();
        CxfModule.extend(binder).addCustomConfigurer(EndpointImpl.class, EndpointConfigurer.class, true);

        JettyModule.extend(binder).addMappedServlet(new TypeLiteral<MappedServlet<AbstractHTTPServlet>>() {
        });
    }

    @Provides
    @Singleton
    public MappedServlet<AbstractHTTPServlet> provideServlet(ConfigurationFactory configFactory, Set<Endpoint> endpoints) {

        // TODO the sole purpose of endpoints parameter is a side effect of eager init of endpoints. Is there a CXF
        //   API that actually connects them to the servlet?

        return configFactory.config(CxfJaxwsServletFactory.class, CONFIG_PREFIX).createServlet();
    }

    @Provides
    @Singleton
    public EndpointConfigurer provideEndpointConfigurer(
            @CxfInterceptorsServerIn Set<Interceptor<? extends Message>> inInterceptors,
            @CxfInterceptorsServerOut Set<Interceptor<? extends Message>> outInterceptors,
            @CxfInterceptorsServerInFault Set<Interceptor<? extends Message>> inFaultInterceptors,
            @CxfInterceptorsServerOutFault Set<Interceptor<? extends Message>> outFaultInterceptors,
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
