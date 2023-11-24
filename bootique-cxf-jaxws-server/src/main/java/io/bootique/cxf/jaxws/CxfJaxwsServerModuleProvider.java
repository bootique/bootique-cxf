package io.bootique.cxf.jaxws;

import io.bootique.BQModuleProvider;
import io.bootique.bootstrap.BuiltModule;
import io.bootique.cxf.CxfModuleProvider;
import io.bootique.jetty.JettyModuleProvider;

import java.util.Collection;

import static java.util.Arrays.asList;

public class CxfJaxwsServerModuleProvider implements BQModuleProvider {

    @Override
    public BuiltModule buildModule() {
        return BuiltModule.of(new CxfJaxwsServerModule())
                .provider(this)
                .description("Integrates Apache CXF JAX-WS server engine")
                .config("cxfjaxwsserver", CxfJaxwsServletFactory.class)
                .build();
    }

    @Override
    public Collection<BQModuleProvider> dependencies() {
        return asList(
                new JettyModuleProvider(),
                new CxfModuleProvider()
        );
    }
}
