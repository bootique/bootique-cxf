package io.bootique.cxf;

import io.bootique.ConfigModule;
import io.bootique.cxf.conf.CustomConfigurer;
import io.bootique.cxf.conf.BQBeanLocator;
import io.bootique.cxf.conf.GuiceConfigurer;
import io.bootique.di.Binder;
import io.bootique.di.Injector;
import io.bootique.di.Provides;
import io.bootique.di.TypeLiteral;
import org.apache.cxf.Bus;
import org.apache.cxf.configuration.ConfiguredBeanLocator;
import org.apache.cxf.configuration.Configurer;

import java.util.List;
import java.util.Map;
import javax.inject.Named;
import javax.inject.Singleton;

public class CxfModule extends ConfigModule {

    public static CxfModuleExtender extend(Binder binder) {
        return new CxfModuleExtender(binder);
    }

    @Override
    public void configure(Binder binder) {
        CxfModule.extend(binder).initAllExtensions();
        binder.bind(Bus.class).toProvider(BusProvider.class).initOnStartup();

        // TODO: is this call necessary?
//        binder.requestStaticInjection(GuiceBusFactory.class);
    }

    @Provides
    @Singleton
    public Configurer provideConfigurer(
            Injector injector,
            @Named("custom") Map<TypeLiteral<?>, CustomConfigurer<?>> customConfigurers,
            @Named("default") Map<TypeLiteral<?>, CustomConfigurer<?>> defaultConfigurers
    ) {

        Map<TypeLiteral<?>, List<CustomConfigurer<?>>> mergedConfigurers = MapUtils.mergeMaps(
                defaultConfigurers,
                customConfigurers
        );

        return new GuiceConfigurer(injector, mergedConfigurers);
    }

    @Provides
    @Singleton
    public ConfiguredBeanLocator provideLocator(Injector injector) {
        return new BQBeanLocator(injector);
    }
}
