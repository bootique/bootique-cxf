package io.bootique.cxf;

import com.google.inject.*;
import io.bootique.ConfigModule;
import io.bootique.cxf.conf.CustomConfigurer;
import io.bootique.cxf.conf.GuiceBeanLocator;
import io.bootique.cxf.conf.GuiceConfigurer;
import io.bootique.cxf.conf.MultisourceBeanLocator;
import io.bootique.cxf.interceptor.CxfInterceptors;
import io.bootique.shutdown.ShutdownManager;
import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.bus.extension.ExtensionManagerBus;
import org.apache.cxf.configuration.ConfiguredBeanLocator;
import org.apache.cxf.configuration.Configurer;
import org.apache.cxf.feature.Feature;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.message.Message;

import java.util.Map;
import java.util.Set;

public class CxfModule extends ConfigModule {

    public static CxfModuleExtender extend(Binder binder) {
        return new CxfModuleExtender(binder);
    }

    @Override
    public void configure(Binder binder) {
        CxfModule.extend(binder).initAllExtensions();

        binder.requestStaticInjection(GuiceBusFactory.class);
    }

    @Provides
    @Singleton
    public Bus provideBus(
            Configurer configurer,
            ConfiguredBeanLocator beanLocator,
            Set<Feature> features,
            ShutdownManager shutdownManager,
            @CxfInterceptors(type = CxfInterceptors.Type.IN) Set<Interceptor<? extends Message>> inInterceptors,
            @CxfInterceptors(type = CxfInterceptors.Type.OUT)  Set<Interceptor<? extends Message>> outInterceptors,
            @CxfInterceptors(type = CxfInterceptors.Type.IN_FAULT)  Set<Interceptor<? extends Message>> inFaultInterceptors,
            @CxfInterceptors(type = CxfInterceptors.Type.OUT_FAULT)  Set<Interceptor<? extends Message>> outFaultInterceptors
    ) {

        ExtensionManagerBus bus = new ExtensionManagerBus();


        ConfiguredBeanLocator originalLocator = bus.getExtension(ConfiguredBeanLocator.class);

        MultisourceBeanLocator multisourceBeanLocator = new MultisourceBeanLocator(beanLocator, originalLocator);

        bus.setExtension(multisourceBeanLocator, ConfiguredBeanLocator.class);
        bus.setExtension(configurer, Configurer.class);

        bus.setFeatures(features);

        bus.getInInterceptors().addAll(inInterceptors);
        bus.getInFaultInterceptors().addAll(inFaultInterceptors);
        bus.getOutInterceptors().addAll(outInterceptors);
        bus.getOutFaultInterceptors().addAll(outFaultInterceptors);

        bus.initialize();

        BusFactory.possiblySetDefaultBus(bus);

        shutdownManager.addShutdownHook(bus::shutdown);


        return bus;
    }

    @Provides
    @Singleton
    public Configurer provideConfigurer(Injector injector, Map<TypeLiteral, CustomConfigurer> customConfigurers) {
        return new GuiceConfigurer(injector, customConfigurers);
    }

    @Provides
    @Singleton
    public ConfiguredBeanLocator provideLocator(Injector injector) {
        return new GuiceBeanLocator(injector);
    }
}
