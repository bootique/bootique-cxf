package io.bootique.cxf.jaxws;

import com.google.inject.Module;
import io.bootique.BQModuleProvider;

public class CxfJaxwsServerModuleProvider implements BQModuleProvider {
    @Override
    public Module module() {
        return new CxfJaxwsServerModule();
    }
}
