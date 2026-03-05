package io.bootique.cxf;

import io.bootique.junit.BQModuleTester;
import io.bootique.junit.BQTest;
import org.junit.jupiter.api.Test;

@BQTest
public class CxfModuleTest {

    @Test
    public void check() {
        BQModuleTester.of(CxfModule.class).testAutoLoadable().testConfig();
    }
}
