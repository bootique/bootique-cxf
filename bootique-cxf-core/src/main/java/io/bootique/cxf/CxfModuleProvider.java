package io.bootique.cxf;

import com.google.inject.Module;
import io.bootique.BQModuleProvider;

public class CxfModuleProvider implements BQModuleProvider {
    @Override
    public Module module() {
        return new CxfModule();
    }
}
