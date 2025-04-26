package io.bootique.cxf;

import io.bootique.ModuleExtender;
import io.bootique.cxf.conf.CustomConfigurer;
import io.bootique.cxf.interceptor.CxfInterceptorAnnotationHolder;
import io.bootique.cxf.interceptor.CxfInterceptorsIn;
import io.bootique.cxf.interceptor.CxfInterceptorsInFault;
import io.bootique.cxf.interceptor.CxfInterceptorsOut;
import io.bootique.cxf.interceptor.CxfInterceptorsOutFault;
import io.bootique.cxf.interceptor.InterceptorsContributor;
import io.bootique.di.Binder;
import io.bootique.di.MapBuilder;
import io.bootique.di.SetBuilder;
import io.bootique.di.TypeLiteral;
import org.apache.cxf.feature.Feature;

public class CxfModuleExtender extends ModuleExtender<CxfModuleExtender> {


    public static final CxfInterceptorAnnotationHolder BUS_INTERCEPTORS = new CxfInterceptorAnnotationHolder(
            CxfInterceptorsIn.class,
            CxfInterceptorsInFault.class,
            CxfInterceptorsOut.class,
            CxfInterceptorsOutFault.class
    );
    private final InterceptorsContributor interceptorsContributor;
    private MapBuilder<TypeLiteral<?>, CustomConfigurer<?>> customConfigurers;
    private MapBuilder<TypeLiteral<?>, CustomConfigurer<?>> defaultConfigurers;
    private SetBuilder<Feature> features;


    public CxfModuleExtender(Binder binder) {
        super(binder);

        interceptorsContributor = new InterceptorsContributor(BUS_INTERCEPTORS, binder);
    }

    @Override
    public CxfModuleExtender initAllExtensions() {

        contributeCustomConfigurers();
        contributeDefaultConfigurers();
        contributeCxfFeatures();

        interceptorsContributor.init();
        return this;
    }

    public InterceptorsContributor contributeBusInterceptors() {
        return interceptorsContributor;
    }

    public <T, V extends CustomConfigurer<T>> CxfModuleExtender addCustomConfigurer(Class<T> configurable,
                                                                                    Class<V> configurer) {
        addCustomConfigurer(configurable, configurer, false);
        return this;
    }

    public <T, V extends CustomConfigurer<T>> CxfModuleExtender addCustomConfigurer(Class<T> configurable,
                                                                                    Class<V> configurer,
                                                                                    boolean overrideDefault) {
        MapBuilder<TypeLiteral<?>, CustomConfigurer<?>> configurers = overrideDefault ? contributeDefaultConfigurers() : contributeCustomConfigurers();
        configurers.put(TypeLiteral.of(configurable), configurer);
        return this;
    }


    public <T extends Feature> CxfModuleExtender addFeature(Class<T> feature) {
        contributeCxfFeatures().add(feature);

        return this;
    }

    public <T extends Feature> CxfModuleExtender addFeature(T feature) {
        contributeCxfFeatures().addInstance(feature);

        return this;
    }

    private SetBuilder<Feature> contributeCxfFeatures() {
        if (features == null) {
            features = newSet(Feature.class);
        }

        return features;
    }

    private MapBuilder<TypeLiteral<?>, CustomConfigurer<?>> contributeCustomConfigurers() {
        if (customConfigurers == null) {
            customConfigurers = binder.bindMap(new TypeLiteral<TypeLiteral<?>>(){}, new TypeLiteral<CustomConfigurer<?>>(){},"custom");
        }

        return customConfigurers;
    }


    private MapBuilder<TypeLiteral<?>, CustomConfigurer<?>> contributeDefaultConfigurers() {
        if (defaultConfigurers == null) {
            defaultConfigurers = binder.bindMap(new TypeLiteral<TypeLiteral<?>>(){}, new TypeLiteral<CustomConfigurer<?>>(){},"default");
        }

        return defaultConfigurers;
    }

}
