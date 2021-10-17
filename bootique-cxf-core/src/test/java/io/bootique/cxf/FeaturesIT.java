package io.bootique.cxf;

import io.bootique.BQRuntime;
import io.bootique.junit5.BQTest;
import io.bootique.junit5.BQTestFactory;
import io.bootique.junit5.BQTestTool;
import org.apache.cxf.Bus;
import org.apache.cxf.feature.AbstractFeature;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@BQTest
public class FeaturesIT {

    @BQTestTool
    final BQTestFactory testFactory = new BQTestFactory().autoLoadModules();

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

        Assertions.assertTrue(Feature1.LOADED);
    }


}
