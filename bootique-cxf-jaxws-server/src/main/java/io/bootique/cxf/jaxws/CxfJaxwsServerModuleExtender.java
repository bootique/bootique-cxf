package io.bootique.cxf.jaxws;

import io.bootique.ModuleExtender;
import io.bootique.cxf.interceptor.CxfInterceptorAnnotationHolder;
import io.bootique.cxf.interceptor.InterceptorsContributor;
import io.bootique.cxf.jaxws.annotation.CxfInterceptorsServerIn;
import io.bootique.cxf.jaxws.annotation.CxfInterceptorsServerInFault;
import io.bootique.cxf.jaxws.annotation.CxfInterceptorsServerOut;
import io.bootique.cxf.jaxws.annotation.CxfInterceptorsServerOutFault;
import io.bootique.di.Binder;
import io.bootique.di.SetBuilder;
import jakarta.xml.ws.Endpoint;

import jakarta.inject.Provider;

public class CxfJaxwsServerModuleExtender extends ModuleExtender<CxfJaxwsServerModuleExtender> {

    public static final CxfInterceptorAnnotationHolder JAX_WS_SERVER_INTERCEPTORS = new CxfInterceptorAnnotationHolder(
            CxfInterceptorsServerIn.class,
            CxfInterceptorsServerInFault.class,
            CxfInterceptorsServerOut.class,
            CxfInterceptorsServerOutFault.class
    );
    private final InterceptorsContributor interceptorsContributor;
    private SetBuilder<Endpoint> endpoints;

    public CxfJaxwsServerModuleExtender(Binder binder) {
        super(binder);
        interceptorsContributor = new InterceptorsContributor(JAX_WS_SERVER_INTERCEPTORS, binder);
    }

    @Override
    public CxfJaxwsServerModuleExtender initAllExtensions() {
        contributeEndpoints();
        interceptorsContributor.init();
        return this;
    }

    public CxfJaxwsServerModuleExtender addEndpoint(Class<? extends Provider<? extends Endpoint>> endpointProvider) {
        contributeEndpoints().addProvider(endpointProvider);
        return this;
    }

    public CxfJaxwsServerModuleExtender addEndpoint(Provider<? extends Endpoint> endpointProvider) {
        contributeEndpoints().addProviderInstance(endpointProvider);
        return this;
    }

    public InterceptorsContributor contributeServerInterceptors() {
        return interceptorsContributor;
    }

    private SetBuilder<Endpoint> contributeEndpoints() {
        if (endpoints == null) {
            endpoints = newSet(Endpoint.class);
        }

        return endpoints;
    }
}
