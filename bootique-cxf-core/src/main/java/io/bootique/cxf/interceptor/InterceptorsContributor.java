package io.bootique.cxf.interceptor;

import com.google.inject.Binder;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.message.Message;

public class InterceptorsContributor {

    private static TypeLiteral<Interceptor<? extends Message>> INTERCEPTOR_TYPE_LITERAL = new TypeLiteral<Interceptor<? extends Message>>(){};

    private final String target;
    private final Binder binder;

    private Multibinder<Interceptor<? extends Message>> inInterceptors;
    private Multibinder<Interceptor<? extends Message>> outInterceptors;
    private Multibinder<Interceptor<? extends Message>> inFaultInterceptors;
    private Multibinder<Interceptor<? extends Message>> outFaultInterceptors;

    public InterceptorsContributor(String target, Binder binder) {
        this.target = target;
        this.binder = binder;


    }

    public void init() {
        contributeInInterceptors();
        contributeOutInterceptors();
        contributeInFaultInterceptors();
        contributeOutFaultInterceptors();
    }

    public <T extends Message, V extends Interceptor<T>> InterceptorsContributor addInInterceptor(Class<V> interceptorClass) {
        contributeInInterceptors().addBinding().to(interceptorClass);

        return this;
    }

    public <T extends Message, V extends Interceptor<T>> InterceptorsContributor addInInterceptor(V interceptor) {
        contributeInInterceptors().addBinding().toInstance(interceptor);

        return this;
    }

    public <T extends Message, V extends Interceptor<T>> InterceptorsContributor addOutInterceptor(Class<V> interceptorClass) {
        contributeOutInterceptors().addBinding().to(interceptorClass);

        return this;
    }

    public <T extends Message, V extends Interceptor<T>> InterceptorsContributor addOutInterceptor(V interceptor) {
        contributeOutInterceptors().addBinding().toInstance(interceptor);

        return this;
    }


    public <T extends Message, V extends Interceptor<T>> InterceptorsContributor addInFaultInterceptor(Class<V> interceptorClass) {
        contributeInFaultInterceptors().addBinding().to(interceptorClass);

        return this;
    }

    public <T extends Message, V extends Interceptor<T>> InterceptorsContributor addInFaultInterceptor(V interceptor) {
        contributeInFaultInterceptors().addBinding().toInstance(interceptor);

        return this;
    }

    public <T extends Message, V extends Interceptor<T>> InterceptorsContributor addOutFaultInterceptor(Class<V> interceptorClass) {
        contributeOutFaultInterceptors().addBinding().to(interceptorClass);

        return this;
    }

    public <T extends Message, V extends Interceptor<T>> InterceptorsContributor addOutFaultInterceptor(V interceptor) {
        contributeOutFaultInterceptors().addBinding().toInstance(interceptor);

        return this;
    }

    private Multibinder<Interceptor<? extends Message>> contributeInInterceptors() {

        if (inInterceptors == null) {
            inInterceptors = Multibinder.newSetBinder(binder, INTERCEPTOR_TYPE_LITERAL, new CxfInterceptorsImpl(target, CxfInterceptors.Type.IN));
        }

        return inInterceptors;
    }

    private Multibinder<Interceptor<? extends Message>> contributeInFaultInterceptors() {

        if (inFaultInterceptors == null) {
            inFaultInterceptors = Multibinder.newSetBinder(binder, INTERCEPTOR_TYPE_LITERAL, new CxfInterceptorsImpl(target, CxfInterceptors.Type.IN_FAULT));
        }

        return inFaultInterceptors;
    }

    private Multibinder<Interceptor<? extends Message>> contributeOutInterceptors() {

        if (outInterceptors == null) {
            outInterceptors = Multibinder.newSetBinder(binder, INTERCEPTOR_TYPE_LITERAL, new CxfInterceptorsImpl(target, CxfInterceptors.Type.OUT));
        }

        return outInterceptors;
    }

    private Multibinder<Interceptor<? extends Message>> contributeOutFaultInterceptors() {

        if (outFaultInterceptors == null) {
            outFaultInterceptors = Multibinder.newSetBinder(binder, INTERCEPTOR_TYPE_LITERAL, new CxfInterceptorsImpl(target, CxfInterceptors.Type.OUT_FAULT));
        }

        return outFaultInterceptors;
    }


}
