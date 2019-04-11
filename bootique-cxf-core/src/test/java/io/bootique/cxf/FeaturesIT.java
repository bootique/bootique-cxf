package io.bootique.cxf;

import io.bootique.BQRuntime;
import io.bootique.test.junit.BQTestFactory;
import org.apache.cxf.Bus;
import org.apache.cxf.feature.AbstractFeature;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

public class FeaturesIT {

    @Rule
    public BQTestFactory testFactory = new BQTestFactory().autoLoadModules();

    public static class Feature1 extends AbstractFeature {

        public static Boolean LOADED = false;

        @Override
        public void initialize(Bus bus) {
            LOADED = true;
        }
    }

    @Test
    public void testFeatureIsLoaded() {
        BQRuntime runtime = testFactory.app().module(binder -> {
            CxfModule.extend(binder).addFeature(Feature1.class);
        }).createRuntime();

        runtime.getInstance(Bus.class);

        Assert.assertTrue(Feature1.LOADED);
    }



}
