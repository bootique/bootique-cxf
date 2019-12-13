package io.bootique.cxf;

import io.bootique.BQRuntime;
import io.bootique.di.Key;
import io.bootique.test.junit.BQTestFactory;
import org.apache.cxf.Bus;
import org.apache.cxf.bus.extension.ExtensionManagerBus;
import org.apache.cxf.configuration.ConfiguredBeanLocator;
import org.apache.cxf.configuration.Configurer;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

public class CxfModuleIT {

    @Rule
    public BQTestFactory testFactory = new BQTestFactory().autoLoadModules();

    static class TestBus extends ExtensionManagerBus {}

    @Test
    public void testOverrideBusImplementation() {

        BQRuntime runtime = testFactory.app().override(CxfModule.class).with(binder -> {
            binder.bind(Bus.class).to(TestBus.class);
        }).createRuntime();

        Assert.assertEquals(TestBus.class, runtime.getInstance(Key.get(Bus.class)).getClass());
        Assert.assertNotNull(runtime.getInstance(Key.get(Configurer.class)));
        Assert.assertNotNull(runtime.getInstance(Key.get(ConfiguredBeanLocator.class)));

    }

    @Test
    public void testDefaultContents() {
        BQRuntime runtime = testFactory.app().createRuntime();

        Assert.assertNotNull(runtime.getInstance(Key.get(Bus.class)));
        Assert.assertNotNull(runtime.getInstance(Key.get(Configurer.class)));
        Assert.assertNotNull(runtime.getInstance(Key.get(ConfiguredBeanLocator.class)));
    }
}
