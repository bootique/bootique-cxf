package io.bootique.cxf.jaxws;

import io.bootique.BQModuleProvider;
import io.bootique.cxf.CxfModuleProvider;
import io.bootique.di.BQModule;
import io.bootique.jetty.JettyModuleProvider;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import static java.util.Arrays.asList;

public class CxfJaxwsServerModuleProvider implements BQModuleProvider {
    @Override
    public BQModule module() {
        return new CxfJaxwsServerModule();
    }

    @Override
    public Map<String, Type> configs() {
        return Collections.singletonMap("cxfjaxwsserver", CxfJaxwsServletFactory.class);
    }

    @Override
    public Collection<BQModuleProvider> dependencies() {
        return asList(
                new JettyModuleProvider(),
                new CxfModuleProvider()
        );
    }
}
