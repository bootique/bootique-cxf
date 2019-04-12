package io.bootique.cxf;

import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import io.bootique.ConfigModule;
import io.bootique.cxf.conf.CustomConfigurer;
import io.bootique.cxf.conf.GuiceBeanLocator;
import io.bootique.cxf.conf.GuiceConfigurer;
import org.apache.cxf.Bus;
import org.apache.cxf.configuration.ConfiguredBeanLocator;
import org.apache.cxf.configuration.Configurer;

import java.util.List;
import java.util.Map;

public class CxfModule extends ConfigModule {

    public static CxfModuleExtender extend(Binder binder) {
        return new CxfModuleExtender(binder);
    }

    @Override
    public void configure(Binder binder) {
        CxfModule.extend(binder).initAllExtensions();
        binder.bind(Bus.class).toProvider(BusProvider.class).asEagerSingleton();

        binder.requestStaticInjection(GuiceBusFactory.class);
    }


    @Provides
    @Singleton
    public Configurer provideConfigurer(
            Injector injector,
            @Named("custom") Map<TypeLiteral, CustomConfigurer> customConfigurers,
            @Named("default") Map<TypeLiteral, CustomConfigurer> defaultConfigurers
    ) {

        Map<TypeLiteral, List<CustomConfigurer>> mergedConfigurers = MapUtils.mergeMaps(
                defaultConfigurers,
                customConfigurers
        );

        return new GuiceConfigurer(injector, mergedConfigurers);
    }

    @Provides
    @Singleton
    public ConfiguredBeanLocator provideLocator(Injector injector) {
        return new GuiceBeanLocator(injector);
    }
}
