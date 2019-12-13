package io.bootique.cxf;

import org.apache.cxf.Bus;
import org.apache.cxf.bus.CXFBusFactory;

import java.util.Map;
import javax.inject.Inject;
import javax.inject.Provider;

/**
 * Bridge to provide Guice managed factory for all classes, that will attempt to access Bus from static context
 */
public class GuiceBusFactory extends CXFBusFactory {

    @Inject
    private Provider<Bus> busProvider;

    public GuiceBusFactory() {
        System.out.println("test");
    }

    @Override
    public Bus createBus(Map<Class<?>, Object> e, Map<String, Object> properties) {
        return busProvider.get();
    }
}
