package io.bootique.cxf.jaxws;

import io.bootique.BQRuntime;
import io.bootique.cxf.CxfModule;
import io.bootique.jetty.JettyModule;
import io.bootique.junit5.*;
import org.junit.jupiter.api.Test;

@BQTest
public class CxfJaxwsServerModuleProviderIT {

    @BQTestTool
    final BQTestFactory testFactory = new BQTestFactory();

    @Test
    public void testAutoLoadable() {
        BQModuleProviderChecker.testAutoLoadable(CxfJaxwsServerModuleProvider.class);
    }

    @Test
    public void testModuleDeclaresDependencies() {
        final BQRuntime bqRuntime = testFactory.app().moduleProvider(new CxfJaxwsServerModuleProvider()).createRuntime();
        BQRuntimeChecker.testModulesLoaded(bqRuntime,
                JettyModule.class,
                CxfModule.class,
                CxfJaxwsServerModule.class
        );
    }
}
