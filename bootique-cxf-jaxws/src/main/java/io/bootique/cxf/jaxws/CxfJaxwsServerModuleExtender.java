package io.bootique.cxf.jaxws;

import com.google.inject.Binder;
import com.google.inject.Provider;
import com.google.inject.multibindings.Multibinder;
import io.bootique.ModuleExtender;
import io.bootique.cxf.interceptor.InterceptorsContributor;

import javax.xml.ws.Endpoint;

public class CxfJaxwsServerModuleExtender extends ModuleExtender<CxfJaxwsServerModuleExtender> {


    public static final String JAX_WS_SERVER_INTERCEPTORS = "jaxwsserver";
    private final InterceptorsContributor interceptorsContributor;
    private Multibinder<Endpoint> endpoints;


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

    public CxfJaxwsServerModuleExtender addEndpoint(Provider<? extends Endpoint> endpointProvider) {

        contributeEndpoints().addBinding().toProvider(endpointProvider);

        return this;
    }

    public InterceptorsContributor contributeServerInterceptors() {
        return interceptorsContributor;
    }

    private Multibinder<Endpoint> contributeEndpoints() {
        if (endpoints == null) {
            endpoints = newSet(Endpoint.class);
        }

        return endpoints;
    }


}
