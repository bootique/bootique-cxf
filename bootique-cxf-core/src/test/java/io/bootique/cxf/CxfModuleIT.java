package io.bootique.cxf;

import io.bootique.BQRuntime;
import io.bootique.di.Key;
import io.bootique.junit5.BQTest;
import io.bootique.junit5.BQTestFactory;
import io.bootique.junit5.BQTestTool;
import org.apache.cxf.Bus;
import org.apache.cxf.bus.extension.ExtensionManagerBus;
import org.apache.cxf.configuration.ConfiguredBeanLocator;
import org.apache.cxf.configuration.Configurer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@BQTest
public class CxfModuleIT {

    @BQTestTool
    public BQTestFactory testFactory = new BQTestFactory().autoLoadModules();

    static class TestBus extends ExtensionManagerBus {}

    @Test
    public void overrideBusImplementation() {

        BQRuntime runtime = testFactory.app()
                .override(CxfModule.class)
                .with(binder -> binder.bind(Bus.class).to(TestBus.class)).createRuntime();

        assertEquals(TestBus.class, runtime.getInstance(Key.get(Bus.class)).getClass());
        assertNotNull(runtime.getInstance(Key.get(Configurer.class)));
        assertNotNull(runtime.getInstance(Key.get(ConfiguredBeanLocator.class)));

    }

    @Test
    public void defaultContents() {
        BQRuntime runtime = testFactory.app().createRuntime();

        assertNotNull(runtime.getInstance(Key.get(Bus.class)));
        assertNotNull(runtime.getInstance(Key.get(Configurer.class)));
        assertNotNull(runtime.getInstance(Key.get(ConfiguredBeanLocator.class)));
    }
}
