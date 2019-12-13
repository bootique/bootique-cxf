package io.bootique.cxf.jaxws;

import io.bootique.BQModuleProvider;
import io.bootique.cxf.CxfModuleProvider;
import io.bootique.di.BQModule;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class CxfJaxwsClientModuleProvider implements BQModuleProvider {
    @Override
    public BQModule module() {
        return new CxfJaxwsClientModule();
    }

    @Override
    public Collection<BQModuleProvider> dependencies() {
        return Collections.singletonList(new CxfModuleProvider());
    }

    @Override
    public Map<String, Type> configs() {
        return Collections.singletonMap(CxfJaxwsClientModule.CONF_PREFIX, CxfJaxwsClientConfiguration.class);
    }
}
