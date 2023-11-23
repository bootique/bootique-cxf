package io.bootique.cxf.jaxws;

import io.bootique.BQRuntime;
import io.bootique.cxf.CxfModule;
import io.bootique.junit5.*;
import org.junit.jupiter.api.Test;

@BQTest
public class CxfJaxwsClientModuleProviderIT {

    @BQTestTool
    final BQTestFactory testFactory = new BQTestFactory();

    @Test
    public void autoLoadable() {
        BQModuleProviderChecker.testAutoLoadable(CxfJaxwsClientModuleProvider.class);
    }

    @Test
    public void moduleDeclaresDependencies() {
        final BQRuntime bqRuntime = testFactory.app().moduleProvider(new CxfJaxwsClientModuleProvider()).createRuntime();
        BQRuntimeChecker.testModulesLoaded(bqRuntime,
                CxfModule.class,
                CxfJaxwsClientModule.class
        );
    }
}
