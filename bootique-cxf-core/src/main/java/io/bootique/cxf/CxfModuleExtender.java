package io.bootique.cxf;

import com.google.inject.Binder;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.multibindings.Multibinder;
import io.bootique.ModuleExtender;
import io.bootique.cxf.conf.CustomConfigurer;
import io.bootique.cxf.interceptor.InterceptorsContributor;
import org.apache.cxf.feature.Feature;

public class CxfModuleExtender extends ModuleExtender<CxfModuleExtender> {


    public static final String BUS_INTERCEPTORS = "bus";
    private final InterceptorsContributor interceptorsContributor;
    private MapBinder<TypeLiteral, CustomConfigurer> customConfigurers;
    private Multibinder<Feature> features;


    public CxfModuleExtender(Binder binder) {
        super(binder);

        interceptorsContributor = new InterceptorsContributor(BUS_INTERCEPTORS, binder);
    }

    @Override
    public CxfModuleExtender initAllExtensions() {

        contributeCustomConfigurers();
        contributeCxfFeatures();

        interceptorsContributor.init();
        return this;
    }

    public InterceptorsContributor contributeBusInterceptors() {
        return interceptorsContributor;
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
