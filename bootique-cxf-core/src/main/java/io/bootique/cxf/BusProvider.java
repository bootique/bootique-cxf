package io.bootique.cxf;

import com.google.inject.Inject;
import com.google.inject.Provider;
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

import java.util.Set;

public class BusProvider implements Provider<Bus> {

    private final Configurer configurer;
    private final ConfiguredBeanLocator beanLocator;
    private final Set<Feature> features;
    private final ShutdownManager shutdownManager;
    private final Set<Interceptor<? extends Message>> inInterceptors;
    private final Set<Interceptor<? extends Message>> outInterceptors;
    private final Set<Interceptor<? extends Message>> inFaultInterceptors;
    private final Set<Interceptor<? extends Message>> outFaultInterceptors;

    @Inject
    public BusProvider(
            Configurer configurer,
            ConfiguredBeanLocator beanLocator,
            Set<Feature> features,
            ShutdownManager shutdownManager,
            @CxfInterceptors(type = CxfInterceptors.Type.IN) Set<Interceptor<? extends Message>> inInterceptors,
            @CxfInterceptors(type = CxfInterceptors.Type.OUT)  Set<Interceptor<? extends Message>> outInterceptors,
            @CxfInterceptors(type = CxfInterceptors.Type.IN_FAULT)  Set<Interceptor<? extends Message>> inFaultInterceptors,
            @CxfInterceptors(type = CxfInterceptors.Type.OUT_FAULT)  Set<Interceptor<? extends Message>> outFaultInterceptors
    ) {

        this.configurer = configurer;
        this.beanLocator = beanLocator;
        this.features = features;
        this.shutdownManager = shutdownManager;
        this.inInterceptors = inInterceptors;
        this.outInterceptors = outInterceptors;
        this.inFaultInterceptors = inFaultInterceptors;
        this.outFaultInterceptors = outFaultInterceptors;
    }

    @Override
    public Bus get() {
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
}
