package io.bootique.cxf.conf;

import io.bootique.di.DIBootstrap;
import io.bootique.di.Injector;
import io.bootique.di.Key;
import io.bootique.di.SetBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class BqBeanLocatorTest {

    private BQBeanLocator locator;

    @BeforeEach
    public void setUp() {

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
        assertTrue(names.isEmpty());
    }

    @Test
    public void getBeanNamesOfType_namedBeansConfigured() {

        List<String> names = locator.getBeanNamesOfType(Integer.class);

        assertEquals(Arrays.asList("num1","num2"), names);
    }

    @Test
    public void getBeanNamesOfType_regularBeansConfigured() {
        List<String> names = locator.getBeanNamesOfType(String.class);
        assertEquals(Collections.singletonList(String.class.getName()), names);
    }

    @Test
    public void getBeanOfType_noBeansConfigured() {
        assertNull(locator.getBeanOfType(Long.class.getName(), Long.class));
    }

    @Test
    public void getBeanOfType_namedBean() {
        assertEquals(Integer.valueOf(1), locator.getBeanOfType("num1", Integer.class));
        assertEquals(Integer.valueOf(2), locator.getBeanOfType("num2", Integer.class));
    }

    @Test
    public void getBeanOfType_regularBean() {
        assertEquals("test", locator.getBeanOfType(null, String.class));
        assertEquals("test", locator.getBeanOfType(String.class.getName(), String.class));
    }

    @Test
    public void getBeansOfType_noBeansConfigured() {
        Collection<? extends Long> beans = locator.getBeansOfType(Long.class);
        assertTrue(beans.isEmpty());
    }


    @Test
    public void getBeansOfType_namedBeansConfigured() {
        assertEquals(Arrays.asList(1,2), locator.getBeansOfType(Integer.class));
    }

    @Test
    public void getBeansOfType_regularBeansConfigured() {
        assertEquals(Collections.singletonList("test"), locator.getBeansOfType(String.class));
    }

    @Test
    public void getBeansOfType_multibinding() {
        Set<Boolean> booleans = new HashSet<>();
        booleans.add(true);
        booleans.add(false);
        assertEquals(Collections.singletonList(booleans), locator.getBeansOfType(Set.class));
    }

    @Test
    public void hasBeanOfName() {

        // not supporting named beans
        assertFalse(locator.hasBeanOfName("num1"));

        assertFalse(locator.hasBeanOfName(Long.class.getName()));

        assertTrue(locator.hasBeanOfName(Integer.class.getName()));
        assertTrue(locator.hasBeanOfName(String.class.getName()));
        assertTrue(locator.hasBeanOfName(Set.class.getName()));
    }
}