package io.bootique.cxf.conf;

import io.bootique.di.DIBootstrap;
import io.bootique.di.Injector;
import io.bootique.di.Key;
import io.bootique.di.SetBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GuiceBeanLocatorTest {

    private BQBeanLocator locator;

    @Before
    public void setUp() throws Exception {

        Injector injector = DIBootstrap.createInjector(binder -> {
            binder.bind(Key.get(Integer.class, "num1")).toInstance(1);
            binder.bind(Key.get(Integer.class, "num2")).toInstance(2);

            binder.bind(String.class).toInstance("test");

            SetBuilder<Boolean> booleanMultibinder = binder.bindSet(Boolean.class);
            booleanMultibinder.addInstance(true);
            booleanMultibinder.addInstance(false);

        });
        locator = new BQBeanLocator(injector);

    }

    @Test
    public void getBeanNamesOfType_noBeansConfigured() {

        List<String> names = locator.getBeanNamesOfType(Long.class);
        Assert.assertTrue(names.isEmpty());
    }

    @Test
    public void getBeanNamesOfType_namedBeansConfigured() {

        List<String> names = locator.getBeanNamesOfType(Integer.class);

        Assert.assertEquals(Arrays.asList("num1","num2"), names);
    }

    @Test
    public void getBeanNamesOfType_regularBeansConfigured() {
        List<String> names = locator.getBeanNamesOfType(String.class);

        Assert.assertEquals(Collections.singletonList(String.class.getName()), names);
    }

    @Test
    public void getBeanOfType_noBeansConfigured() {

        Assert.assertNull(locator.getBeanOfType(Long.class.getName(), Long.class));
    }

    @Test
    public void getBeanOfType_namedBean() {
        Assert.assertEquals(Integer.valueOf(1), locator.getBeanOfType("num1", Integer.class));
        Assert.assertEquals(Integer.valueOf(2), locator.getBeanOfType("num2", Integer.class));
    }

    @Test
    public void getBeanOfType_regularBean() {
        Assert.assertEquals("test", locator.getBeanOfType(null, String.class));
        Assert.assertEquals("test", locator.getBeanOfType(String.class.getName(), String.class));
    }

    @Test
    public void getBeansOfType_noBeansConfigured() {

        Collection<? extends Long> beans = locator.getBeansOfType(Long.class);
        Assert.assertTrue(beans.isEmpty());
    }


    @Test
    public void getBeansOfType_namedBeansConfigured() {

        Assert.assertEquals(Arrays.asList(1,2), locator.getBeansOfType(Integer.class));
    }

    @Test
    public void getBeansOfType_regularBeansConfigured() {

        Assert.assertEquals(Collections.singletonList("test"), locator.getBeansOfType(String.class));
    }

    @Test
    public void getBeansOfType_multibinding() {
        Set<Boolean> booleans = new HashSet<>();
        booleans.add(true);
        booleans.add(false);

        Assert.assertEquals(Collections.singletonList(booleans), locator.getBeansOfType(Set.class));
    }

    @Test
    public void hasBeanOfName() {

        // not supporting named beans
        Assert.assertFalse(locator.hasBeanOfName("num1"));

        Assert.assertFalse(locator.hasBeanOfName(Long.class.getName()));

        Assert.assertTrue(locator.hasBeanOfName(Integer.class.getName()));
        Assert.assertTrue(locator.hasBeanOfName(String.class.getName()));
        Assert.assertTrue(locator.hasBeanOfName(Set.class.getName()));


    }

}