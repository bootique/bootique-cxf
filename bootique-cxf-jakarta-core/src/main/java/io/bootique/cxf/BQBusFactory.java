package io.bootique.cxf;

import org.apache.cxf.Bus;
import org.apache.cxf.bus.CXFBusFactory;

import jakarta.inject.Inject;
import jakarta.inject.Provider;
import java.util.Map;

/**
 * Bridge to provide Bootique managed factory for all classes, that will attempt to access Bus from static context
 */
public class BQBusFactory extends CXFBusFactory {

    @Inject
    private Provider<Bus> busProvider;

    public BQBusFactory() {
        System.out.println("test");
    }

    @Override
    public Bus createBus(Map<Class<?>, Object> e, Map<String, Object> properties) {
        return busProvider.get();
    }
}
