package io.bootique.cxf.jaxws;

import io.bootique.junit.BQModuleTester;
import io.bootique.junit.BQTest;
import org.junit.jupiter.api.Test;

@BQTest
public class CxfJaxwsClientModuleTest {

    @Test
    public void autoLoadable() {
        BQModuleTester.of(CxfJaxwsClientModule.class).testAutoLoadable().testConfig();
    }

}
