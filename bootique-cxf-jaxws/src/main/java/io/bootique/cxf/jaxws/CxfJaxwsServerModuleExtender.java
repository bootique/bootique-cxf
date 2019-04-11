package io.bootique.cxf.jaxws;

import com.google.inject.Binder;
import com.google.inject.Provider;
import com.google.inject.multibindings.Multibinder;
import io.bootique.ModuleExtender;

import javax.xml.ws.Endpoint;

public class CxfJaxwsServerModuleExtender extends ModuleExtender<CxfJaxwsServerModuleExtender> {


    private Multibinder<Endpoint> endpoints;

    public CxfJaxwsServerModuleExtender(Binder binder) {
        super(binder);
    }



    @Override
    public CxfJaxwsServerModuleExtender initAllExtensions() {

        contributeEndpoints();

        return this;
    }

    public CxfJaxwsServerModuleExtender addEndpoint(Provider<? extends Endpoint> endpointProvider) {

        contributeEndpoints().addBinding().toProvider(endpointProvider);

        return this;
    }

    private Multibinder<Endpoint> contributeEndpoints() {
        if (endpoints == null) {
            endpoints = newSet(Endpoint.class);
        }

        return endpoints;
    }


}
