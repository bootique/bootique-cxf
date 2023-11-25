package io.bootique.cxf;

import io.bootique.junit5.BQModuleProviderChecker;
import io.bootique.junit5.BQTest;
import org.junit.jupiter.api.Test;

@BQTest
public class CxfModuleTest {

    @Test
    public void testAutoLoading() {
        BQModuleProviderChecker.testAutoLoadable(CxfModule.class);
    }
}
