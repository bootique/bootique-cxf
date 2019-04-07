package io.bootique.cxf.jaxws;

import com.google.inject.Module;
import io.bootique.BQModuleProvider;
import io.bootique.cxf.CxfModuleProvider;

import java.util.Collection;

import static java.util.Arrays.asList;

public class CxfJaxwsClientModuleProvider implements BQModuleProvider {
    @Override
    public Module module() {
        return new CxfJaxwsClientModule();
    }

    @Override
    public Collection<BQModuleProvider> dependencies() {
        return asList(
                new CxfModuleProvider()
        );
    }
}
