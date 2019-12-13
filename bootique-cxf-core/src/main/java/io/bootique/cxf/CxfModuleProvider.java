package io.bootique.cxf;

import io.bootique.BQModuleProvider;
import io.bootique.di.BQModule;

public class CxfModuleProvider implements BQModuleProvider {
    @Override
    public BQModule module() {
        return new CxfModule();
    }
}
