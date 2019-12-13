package io.bootique.cxf.jaxws;

import io.bootique.ModuleExtender;
import io.bootique.cxf.interceptor.CxfInterceptorAnnotationHolder;
import io.bootique.cxf.interceptor.InterceptorsContributor;
import io.bootique.cxf.jaxws.annotation.CxfInterceptorsClientIn;
import io.bootique.cxf.jaxws.annotation.CxfInterceptorsClientInFault;
import io.bootique.cxf.jaxws.annotation.CxfInterceptorsClientOut;
import io.bootique.cxf.jaxws.annotation.CxfInterceptorsClientOutFault;
import io.bootique.di.Binder;
import io.bootique.di.SetBuilder;

import javax.xml.ws.Endpoint;

public class CxfJaxwsClientModuleExtender extends ModuleExtender<CxfJaxwsClientModuleExtender> {

    public static final CxfInterceptorAnnotationHolder JAX_WS_CLIENT_INTERCEPTORS = new CxfInterceptorAnnotationHolder(
            CxfInterceptorsClientIn.class,
            CxfInterceptorsClientInFault.class,
            CxfInterceptorsClientOut.class,
            CxfInterceptorsClientOutFault.class
    );
    private final InterceptorsContributor interceptorsContributor;
    private SetBuilder<Endpoint> endpoints;

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
