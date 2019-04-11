package io.bootique.cxf;

import com.google.inject.*;
import io.bootique.ConfigModule;
import io.bootique.cxf.conf.CustomConfigurer;
import io.bootique.cxf.conf.GuiceBeanLocator;
import io.bootique.cxf.conf.GuiceConfigurer;
import io.bootique.cxf.conf.MultisourceBeanLocator;
import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.bus.extension.ExtensionManagerBus;
import org.apache.cxf.configuration.ConfiguredBeanLocator;
import org.apache.cxf.configuration.Configurer;

import java.util.Map;

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
    public Bus provideBus(Configurer configurer, ConfiguredBeanLocator beanLocator) {

        ExtensionManagerBus bus = new ExtensionManagerBus();


        ConfiguredBeanLocator originalLocator = bus.getExtension(ConfiguredBeanLocator.class);

        MultisourceBeanLocator multisourceBeanLocator = new MultisourceBeanLocator(beanLocator, originalLocator);

        bus.setExtension(multisourceBeanLocator, ConfiguredBeanLocator.class);
        bus.setExtension(configurer, Configurer.class);

        bus.initialize();

        BusFactory.possiblySetDefaultBus(bus);

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
