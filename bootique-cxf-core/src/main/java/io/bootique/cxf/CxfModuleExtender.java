package io.bootique.cxf;

import com.google.inject.Binder;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;
import io.bootique.ModuleExtender;
import io.bootique.cxf.conf.CustomConfigurer;
import org.apache.cxf.feature.Feature;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.message.Message;

public class CxfModuleExtender extends ModuleExtender<CxfModuleExtender> {

    public static final String IN_DIRECTION = "inInterceptors";
    public static final String OUT_DIRECTION = "outInterceptors";

    public static final String IN_FAULT_DIRECTION = "outInterceptors";
    public static final String OUT_FAULT_DIRECTION = "outInterceptors";



    private MapBinder<TypeLiteral, CustomConfigurer> customConfigurers;
    private Multibinder<Feature> features;
    private Multibinder<Interceptor<? extends Message>> inInterceptors;
    private Multibinder<Interceptor<? extends Message>> outInterceptors;
    private Multibinder<Interceptor<? extends Message>> inFaultInterceptors;
    private Multibinder<Interceptor<? extends Message>> outFaultInterceptors;

    private TypeLiteral<Interceptor<? extends Message>> interceptorTypeLiteral = new TypeLiteral<Interceptor<? extends Message>>(){};

    public CxfModuleExtender(Binder binder) {
        super(binder);
    }

    @Override
    public CxfModuleExtender initAllExtensions() {

        contributeCustomConfigurers();
        contributeCxfFeatures();

        contributeInInterceptors();
        contributeOutInterceptors();
        contributeInFaultInterceptors();
        contributeOutFaultInterceptors();

        return this;
    }


    public <T, V extends CustomConfigurer<T>> CxfModuleExtender addCustomConfigurer(Class<T> configurable, Class<V> configurer){

        contributeCustomConfigurers().addBinding(TypeLiteral.get(configurable)).to(configurer);
        return this;
    }

    public <T extends Feature> CxfModuleExtender addFeature(Class<T> feature) {
        contributeCxfFeatures().addBinding().to(feature);

        return this;
    }

    public <T extends Feature> CxfModuleExtender addFeature(T feature) {
        contributeCxfFeatures().addBinding().toInstance(feature);

        return this;
    }

    public <T extends Message, V extends Interceptor<T>> CxfModuleExtender addInInterceptor(Class<V> interceptorClass) {
        contributeInInterceptors().addBinding().to(interceptorClass);

        return this;
    }

    public <T extends Message, V extends Interceptor<T>> CxfModuleExtender addInInterceptor(V interceptor) {
        contributeInInterceptors().addBinding().toInstance(interceptor);

        return this;
    }

    public <T extends Message, V extends Interceptor<T>> CxfModuleExtender addOutInterceptor(Class<V> interceptorClass) {
        contributeOutInterceptors().addBinding().to(interceptorClass);

        return this;
    }

    public <T extends Message, V extends Interceptor<T>> CxfModuleExtender addOutInterceptor(V interceptor) {
        contributeOutInterceptors().addBinding().toInstance(interceptor);

        return this;
    }


    public <T extends Message, V extends Interceptor<T>> CxfModuleExtender addInFaultInterceptor(Class<V> interceptorClass) {
        contributeInFaultInterceptors().addBinding().to(interceptorClass);

        return this;
    }

    public <T extends Message, V extends Interceptor<T>> CxfModuleExtender addInFaultInterceptor(V interceptor) {
        contributeInFaultInterceptors().addBinding().toInstance(interceptor);

        return this;
    }

    public <T extends Message, V extends Interceptor<T>> CxfModuleExtender addOutFaultInterceptor(Class<V> interceptorClass) {
        contributeOutFaultInterceptors().addBinding().to(interceptorClass);

        return this;
    }

    public <T extends Message, V extends Interceptor<T>> CxfModuleExtender addOutFaultInterceptor(V interceptor) {
        contributeOutFaultInterceptors().addBinding().toInstance(interceptor);

        return this;
    }


    private Multibinder<Interceptor<? extends Message>> contributeInInterceptors() {

        if (inInterceptors == null) {
            inInterceptors = Multibinder.newSetBinder(binder,interceptorTypeLiteral, Names.named(IN_DIRECTION));
        }

        return inInterceptors;
    }

    private Multibinder<Interceptor<? extends Message>> contributeInFaultInterceptors() {

        if (inFaultInterceptors == null) {
            inFaultInterceptors = Multibinder.newSetBinder(binder,interceptorTypeLiteral, Names.named(IN_FAULT_DIRECTION));
        }

        return inFaultInterceptors;
    }

    private Multibinder<Interceptor<? extends Message>> contributeOutInterceptors() {

        if (outInterceptors == null) {
            outInterceptors = Multibinder.newSetBinder(binder,interceptorTypeLiteral, Names.named(OUT_DIRECTION));
        }

        return outInterceptors;
    }

    private Multibinder<Interceptor<? extends Message>> contributeOutFaultInterceptors() {

        if (outFaultInterceptors == null) {
            outFaultInterceptors = Multibinder.newSetBinder(binder,interceptorTypeLiteral, Names.named(OUT_FAULT_DIRECTION));
        }

        return outFaultInterceptors;
    }


    private Multibinder<Feature> contributeCxfFeatures() {
        if (features == null) {
            features = newSet(Feature.class);
        }

        return features;
    }

    private MapBinder<TypeLiteral, CustomConfigurer> contributeCustomConfigurers() {
        if (customConfigurers == null) {
            customConfigurers = newMap(TypeLiteral.class, CustomConfigurer.class);
        }

        return customConfigurers;
    }


}
