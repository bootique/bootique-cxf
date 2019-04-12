package io.bootique.cxf.jaxws;

import com.google.inject.Binder;
import com.google.inject.multibindings.Multibinder;
import io.bootique.ModuleExtender;
import io.bootique.cxf.interceptor.InterceptorsContributor;

import javax.xml.ws.Endpoint;

public class CxfJaxwsClientModuleExtender extends ModuleExtender<CxfJaxwsClientModuleExtender> {


    public static final String JAX_WS_CLIENT_INTERCEPTORS = "jaxwsclient";
    private final InterceptorsContributor interceptorsContributor;
    private Multibinder<Endpoint> endpoints;


    public CxfJaxwsClientModuleExtender(Binder binder) {
        super(binder);
        interceptorsContributor = new InterceptorsContributor(JAX_WS_CLIENT_INTERCEPTORS, binder);
    }



    @Override
    public CxfJaxwsClientModuleExtender initAllExtensions() {


        interceptorsContributor.init();

        return this;
    }


    /**
     * Gives access to the CXF client interceptors. All added interceptors will be applied only to JAX WS clients.
     * If application has other CXF modules, such as JAX-WS server or JAX-RS, they won't be affected.
     * @return client interceptors contributor
     */
    public InterceptorsContributor contributeClientInterceptors() {
        return interceptorsContributor;
    }



}
