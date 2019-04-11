package io.bootique.cxf.jaxws;

import com.google.inject.Module;
import io.bootique.BQModuleProvider;
import io.bootique.cxf.CxfModuleProvider;
import io.bootique.jetty.JettyModuleProvider;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import static java.util.Arrays.asList;

public class CxfJaxwsServerModuleProvider implements BQModuleProvider {
    @Override
    public Module module() {
        return new CxfJaxwsServerModule();
    }

    @Override
    public Map<String, Type> configs() {
        return Collections.singletonMap(CxfJaxwsServerModule.CONF_PREFIX, CxfJaxwsServletFactory.class);
    }

    @Override
    public Collection<BQModuleProvider> dependencies() {
        return asList(
                new JettyModuleProvider(),
                new CxfModuleProvider()
        );
    }
}
