package io.bootique.cxf.jaxws;

import io.bootique.BQRuntime;
import io.bootique.cxf.CxfModule;
import io.bootique.test.junit.BQModuleProviderChecker;
import io.bootique.test.junit.BQRuntimeChecker;
import io.bootique.test.junit.BQTestFactory;
import org.junit.Rule;
import org.junit.Test;

public class CxfJaxwsClientModuleProviderIT {

    @Rule
    public BQTestFactory testFactory = new BQTestFactory();

    @Test
    public void testAutoLoadable() {
        BQModuleProviderChecker.testAutoLoadable(CxfJaxwsClientModuleProvider.class);
    }

    @Test
    public void testModuleDeclaresDependencies() {
        final BQRuntime bqRuntime = testFactory.app().module(new CxfJaxwsClientModuleProvider()).createRuntime();
        BQRuntimeChecker.testModulesLoaded(bqRuntime,
                CxfModule.class,
                CxfJaxwsClientModule.class
        );
    }
}
