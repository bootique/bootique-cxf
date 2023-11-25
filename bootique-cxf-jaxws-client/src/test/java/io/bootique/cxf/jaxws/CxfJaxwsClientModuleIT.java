package io.bootique.cxf.jaxws;

import io.bootique.BQRuntime;
import io.bootique.cxf.CxfModule;
import io.bootique.junit5.*;
import org.junit.jupiter.api.Test;

@BQTest
public class CxfJaxwsClientModuleIT {

    @BQTestTool
    final BQTestFactory testFactory = new BQTestFactory();

    @Test
    public void autoLoadable() {
        BQModuleProviderChecker.testAutoLoadable(CxfJaxwsClientModule.class);
    }

    @Test
    public void moduleDeclaresDependencies() {
        final BQRuntime bqRuntime = testFactory.app().moduleProvider(new CxfJaxwsClientModule()).createRuntime();
        BQRuntimeChecker.testModulesLoaded(bqRuntime,
                CxfModule.class,
                CxfJaxwsClientModule.class
        );
    }
}
