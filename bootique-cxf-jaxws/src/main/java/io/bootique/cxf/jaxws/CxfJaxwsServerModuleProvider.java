package io.bootique.cxf.jaxws;

import com.google.inject.Module;
import io.bootique.BQModuleProvider;
import io.bootique.cxf.CxfModuleProvider;
import io.bootique.jetty.JettyModuleProvider;

import java.util.Collection;

import static java.util.Arrays.asList;

public class CxfJaxwsServerModuleProvider implements BQModuleProvider {
    @Override
    public Module module() {
        return new CxfJaxwsServerModule();
    }


    @Override
    public Collection<BQModuleProvider> dependencies() {
        return asList(
                new JettyModuleProvider(),
                new CxfModuleProvider()
        );
    }
}
