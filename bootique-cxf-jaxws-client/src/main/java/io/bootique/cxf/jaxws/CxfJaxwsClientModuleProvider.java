package io.bootique.cxf.jaxws;

import io.bootique.BQModuleProvider;
import io.bootique.bootstrap.BuiltModule;
import io.bootique.cxf.CxfModuleProvider;

import java.util.Collection;
import java.util.Collections;

public class CxfJaxwsClientModuleProvider implements BQModuleProvider {

    @Override
    public BuiltModule buildModule() {
        return BuiltModule.of(new CxfJaxwsClientModule())
                .provider(this)
                .description("Integrates Apache CXF JAX-WS client")
                .config("cxfjaxwsclient", CxfJaxwsClientConfiguration.class)
                .build();
    }

    @Override
    public Collection<BQModuleProvider> dependencies() {
        return Collections.singletonList(new CxfModuleProvider());
    }
}
