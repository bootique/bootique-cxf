package io.bootique.cxf;

import io.bootique.test.junit.BQModuleProviderChecker;
import org.junit.Test;

/**
 * Tests bootique module provider contracts.
 *
 * @author Ruslan Ibragimov
 * @since 0.26
 */
public class CxfModuleProviderTest {

    @Test
    public void testAutoLoadable() {
        BQModuleProviderChecker.testAutoLoadable(CxfModuleProvider.class);

    }

    @Test
    public void testMetadata() {
        BQModuleProviderChecker.testMetadata(CxfModuleProvider.class);
    }
}
