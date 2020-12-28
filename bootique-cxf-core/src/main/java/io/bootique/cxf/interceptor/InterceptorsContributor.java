package io.bootique.cxf.interceptor;

import io.bootique.di.Binder;
import io.bootique.di.SetBuilder;
import io.bootique.di.TypeLiteral;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.message.Message;

public class InterceptorsContributor {

    private static TypeLiteral<Interceptor<? extends Message>> INTERCEPTOR_TYPE_LITERAL = new TypeLiteral<Interceptor<? extends Message>>(){};

    private final CxfInterceptorAnnotationHolder annotationHolder;
    private final Binder binder;

    private SetBuilder<Interceptor<? extends Message>> inInterceptors;
    private SetBuilder<Interceptor<? extends Message>> outInterceptors;
    private SetBuilder<Interceptor<? extends Message>> inFaultInterceptors;
    private SetBuilder<Interceptor<? extends Message>> outFaultInterceptors;

    public InterceptorsContributor(CxfInterceptorAnnotationHolder annotationHolder, Binder binder) {
        this.annotationHolder = annotationHolder;
        this.binder = binder;
    }

    public void init() {
        contributeInInterceptors();
        contributeOutInterceptors();
        contributeInFaultInterceptors();
        contributeOutFaultInterceptors();
    }

    public <T extends Message, V extends Interceptor<T>> InterceptorsContributor addInInterceptor(Class<V> interceptorClass) {
        contributeInInterceptors().add(interceptorClass);

        return this;
    }

    public <T extends Message, V extends Interceptor<T>> InterceptorsContributor addInInterceptor(V interceptor) {
        contributeInInterceptors().addInstance(interceptor);

        return this;
    }

    public <T extends Message, V extends Interceptor<T>> InterceptorsContributor addOutInterceptor(Class<V> interceptorClass) {
        contributeOutInterceptors().add(interceptorClass);

        return this;
    }

    public <T extends Message, V extends Interceptor<T>> InterceptorsContributor addOutInterceptor(V interceptor) {
        contributeOutInterceptors().addInstance(interceptor);

        return this;
    }


    public <T extends Message, V extends Interceptor<T>> InterceptorsContributor addInFaultInterceptor(Class<V> interceptorClass) {
        contributeInFaultInterceptors().add(interceptorClass);

        return this;
    }

    public <T extends Message, V extends Interceptor<T>> InterceptorsContributor addInFaultInterceptor(V interceptor) {
        contributeInFaultInterceptors().addInstance(interceptor);

        return this;
    }

    public <T extends Message, V extends Interceptor<T>> InterceptorsContributor addOutFaultInterceptor(Class<V> interceptorClass) {
        contributeOutFaultInterceptors().add(interceptorClass);

        return this;
    }

    public <T extends Message, V extends Interceptor<T>> InterceptorsContributor addOutFaultInterceptor(V interceptor) {
        contributeOutFaultInterceptors().addInstance(interceptor);

        return this;
    }

    private SetBuilder<Interceptor<? extends Message>> contributeInInterceptors() {

        if (inInterceptors == null) {
            inInterceptors = binder.bindSet(INTERCEPTOR_TYPE_LITERAL, annotationHolder.getIn());
        }

        return inInterceptors;
    }

    private SetBuilder<Interceptor<? extends Message>> contributeInFaultInterceptors() {

        if (inFaultInterceptors == null) {
            inFaultInterceptors = binder.bindSet(INTERCEPTOR_TYPE_LITERAL, annotationHolder.getInFault());
        }

        return inFaultInterceptors;
    }

    private SetBuilder<Interceptor<? extends Message>> contributeOutInterceptors() {

        if (outInterceptors == null) {
            outInterceptors = binder.bindSet(INTERCEPTOR_TYPE_LITERAL, annotationHolder.getOut());
        }

        return outInterceptors;
    }

    private SetBuilder<Interceptor<? extends Message>> contributeOutFaultInterceptors() {

        if (outFaultInterceptors == null) {
            outFaultInterceptors = binder.bindSet(INTERCEPTOR_TYPE_LITERAL, annotationHolder.getOutFault());
        }

        return outFaultInterceptors;
    }


}
