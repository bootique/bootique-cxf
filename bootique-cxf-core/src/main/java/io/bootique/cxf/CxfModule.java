package io.bootique.cxf;

import io.bootique.BQModuleProvider;
import io.bootique.bootstrap.BuiltModule;
import io.bootique.cxf.conf.BQBeanLocator;
import io.bootique.cxf.conf.BqConfigurer;
import io.bootique.cxf.conf.CustomConfigurer;
import io.bootique.di.*;
import org.apache.cxf.Bus;
import org.apache.cxf.configuration.ConfiguredBeanLocator;
import org.apache.cxf.configuration.Configurer;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;
import java.util.Map;

public class CxfModule implements BQModule, BQModuleProvider {

    public static CxfModuleExtender extend(Binder binder) {
        return new CxfModuleExtender(binder);
    }

    @Override
    public BuiltModule buildModule() {
        return BuiltModule.of(this)
                .provider(this)
                .description("Integrates Apache CXF core")
                .build();
    }

    @Override
    public void configure(Binder binder) {
        CxfModule.extend(binder).initAllExtensions();
        binder.bind(Bus.class).toProvider(BusProvider.class).initOnStartup();

        // TODO: is this call necessary?
//        binder.requestStaticInjection(BqBusFactory.class);
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

        return new BqConfigurer(injector, mergedConfigurers);
    }

    @Provides
    @Singleton
    public ConfiguredBeanLocator provideLocator(Injector injector) {
        return new BQBeanLocator(injector);
    }
}
