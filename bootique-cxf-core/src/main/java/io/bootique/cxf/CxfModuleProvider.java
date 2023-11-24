package io.bootique.cxf;

import io.bootique.BQModuleProvider;
import io.bootique.bootstrap.BuiltModule;

public class CxfModuleProvider implements BQModuleProvider {

    @Override
    public BuiltModule buildModule() {
        return BuiltModule.of(new CxfModule())
                .provider(this)
                .description("Integrates Apache CXF core")
                .build();
    }
}
