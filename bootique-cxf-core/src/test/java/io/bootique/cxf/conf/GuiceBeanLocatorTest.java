package io.bootique.cxf.conf;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class GuiceBeanLocatorTest {

    private GuiceBeanLocator locator;

    @Before
    public void setUp() throws Exception {

        Injector injector = Guice.createInjector(binder -> {
            binder.bind(Integer.class).annotatedWith(Names.named("num1")).toInstance(1);
            binder.bind(Integer.class).annotatedWith(Names.named("num2")).toInstance(2);

            binder.bind(String.class).toInstance("test");

            Multibinder<Boolean> booleanMultibinder = Multibinder.newSetBinder(binder, Boolean.class);
            booleanMultibinder.addBinding().toInstance(true);
            booleanMultibinder.addBinding().toInstance(false);

        });
        locator = new GuiceBeanLocator(injector);

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

        Assert.assertEquals(Arrays.asList(String.class.getName()), names);
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

        Assert.assertEquals(Arrays.asList("test"), locator.getBeansOfType(String.class));
    }

    @Test
    public void getBeansOfType_multibinding() {

        Assert.assertEquals(Arrays.asList(true, false), locator.getBeansOfType(Boolean.class));
    }

    @Test
    public void hasBeanOfName() {

        // not supporting named beans
        Assert.assertFalse(locator.hasBeanOfName("num1"));

        Assert.assertFalse(locator.hasBeanOfName(Long.class.getName()));

        Assert.assertTrue(locator.hasBeanOfName(Integer.class.getName()));
        Assert.assertTrue(locator.hasBeanOfName(String.class.getName()));
        Assert.assertTrue(locator.hasBeanOfName(Boolean.class.getName()));


    }

}