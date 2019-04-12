package io.bootique.cxf;

import com.google.inject.Inject;
import com.google.inject.Provider;
import org.apache.cxf.Bus;
import org.apache.cxf.bus.CXFBusFactory;

import java.util.Map;

/**
 * Bridge to provide Guice managed factory for all classes, that will attempt to access Bus from static context
 */
public class GuiceBusFactory extends CXFBusFactory {

    @Inject
    static Provider<Bus> busProvider;


    @Override
    public Bus createBus(Map<Class<?>, Object> e, Map<String, Object> properties) {
        return busProvider.get();
    }
}
