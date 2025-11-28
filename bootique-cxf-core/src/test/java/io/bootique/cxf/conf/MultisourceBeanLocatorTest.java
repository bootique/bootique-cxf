package io.bootique.cxf.conf;

import org.apache.cxf.configuration.ConfiguredBeanLocator;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.*;

public class MultisourceBeanLocatorTest {

    @Test
    public void atLeastOneSourceIsRequired() {
        assertThrows(Exception.class, () -> new MultisourceBeanLocator());
    }

    @Test
    public void getBeanNamesOfType_emptySource() {

        List<String> names = new MultisourceBeanLocator(
                new BeanNamesLocator(Long.class, emptyList()),
                new BeanNamesLocator(Long.class, asList("test", "test2"))
        ).getBeanNamesOfType(Long.class);

        assertArrayEquals(asList("test", "test2").toArray(), names.toArray());
    }

    @Test
    public void getBeanNamesOfType_nullSource() {

        List<String> names = new MultisourceBeanLocator(
                new BeanNamesLocator(Long.class, null),
                new BeanNamesLocator(Long.class, asList("test", "test2"))
        ).getBeanNamesOfType(Long.class);

        assertArrayEquals(asList("test", "test2").toArray(), names.toArray());
    }

    @Test
    public void getBeanNamesOfType_mergeResults() {

        List<String> names = new MultisourceBeanLocator(
                new BeanNamesLocator(Long.class, asList("_test", "_test2")),
                new BeanNamesLocator(Long.class, asList("test", "test2"))
        ).getBeanNamesOfType(Long.class);

        assertArrayEquals(asList("_test", "_test2", "test", "test2").toArray(), names.toArray());
    }

    @Test
    public void getBeanOfType_nullFirstSource() {
        Long bean = new MultisourceBeanLocator(
                new BeanLocator(Long.class, null),
                new BeanLocator(Long.class, 3L)
        ).getBeanOfType("test", Long.class);

        assertEquals(3L, bean.longValue());
    }


    @Test
    public void getBeanOfType_nullBothSources() {
        Long bean = new MultisourceBeanLocator(
                new BeanLocator(Long.class, null),
                new BeanLocator(Long.class, null)
        ).getBeanOfType("test", Long.class);

        assertNull(bean);
    }

    @Test
    public void getBeanOfType_notNullBothSources() {

        Long bean = new MultisourceBeanLocator(
                new BeanLocator(Long.class, 2L),
                new BeanLocator(Long.class, 3L)
        ).getBeanOfType("test", Long.class);

        assertEquals(2L, bean.longValue());
    }

    @Test
    public void getBeansOfType_emptySource() {

        Collection<? extends Long> beans = new MultisourceBeanLocator(
                new BeansLocator(Long.class, emptyList()),
                new BeansLocator(Long.class, asList(2L, 3L))
        ).getBeansOfType(Long.class);

        assertArrayEquals(asList(2L, 3L).toArray(), beans.toArray());
    }

    @Test
    public void getBeansOfType_nullSource() {

        Collection<? extends Long> beans = new MultisourceBeanLocator(
                new BeansLocator(Long.class, null),
                new BeansLocator(Long.class, asList(2L, 3L))
        ).getBeansOfType(Long.class);

        assertArrayEquals(asList(2L, 3L).toArray(), beans.toArray());
    }


    @Test
    public void getBeansOfType_mergeSource() {
        Collection<? extends Long> beans = new MultisourceBeanLocator(
                new BeansLocator(Long.class, asList(1L, 3L)),
                new BeansLocator(Long.class, asList(2L, 3L))
        ).getBeansOfType(Long.class);

        assertArrayEquals(asList(1L, 3L, 2L, 3L).toArray(), beans.toArray());
    }

    @Test
    public void loadBeansOfType() {
        BeansLoader l1 = new BeansLoader();
        BeansLoader l2 = new BeansLoader();

        ConfiguredBeanLocator.BeanLoaderListener listener = new ConfiguredBeanLocator.BeanLoaderListener() {
            @Override
            public boolean loadBean(String s, Class aClass) {
                return false;
            }

            @Override
            public boolean beanLoaded(String s, Object o) {
                return false;
            }
        };

        new MultisourceBeanLocator(l1, l2).loadBeansOfType(Long.class, listener);
        assertSame(listener, l1.loadListener);
        assertSame(listener, l2.loadListener);
    }

    @Test
    public void hasConfiguredPropertyValue_firstLocator() {
        assertTrue(new MultisourceBeanLocator(
                new HasPropertyValue("testProp"),
                new HasPropertyValue("other")
        ).hasConfiguredPropertyValue("test", "testProp", "val"));
    }

    @Test
    public void hasConfiguredPropertyValue_secondLocator() {
        assertTrue(new MultisourceBeanLocator(
                new HasPropertyValue("other"),
                new HasPropertyValue("testProp")
        ).hasConfiguredPropertyValue("test", "testProp", "val"));
    }

    @Test
    public void hasConfiguredPropertyValue_noLocator() {
        assertFalse(new MultisourceBeanLocator(
                new HasPropertyValue("other"),
                new HasPropertyValue("other")
        ).hasConfiguredPropertyValue("test", "testProp", "val"));
    }


    @Test
    public void hasBeanOfName_firstLocator() {
        assertTrue(new MultisourceBeanLocator(
                new HasBeanOfName("test"),
                new HasBeanOfName("other")
        ).hasBeanOfName("test"));
    }

    @Test
    public void hasBeanOfName_secondLocator() {
        assertTrue(new MultisourceBeanLocator(
                new HasBeanOfName("other"),
                new HasBeanOfName("test")
        ).hasBeanOfName("test"));
    }

    @Test
    public void hasBeanOfName_noLocator() {
        assertFalse(new MultisourceBeanLocator(
                new HasBeanOfName("other"),
                new HasBeanOfName("other")
        ).hasBeanOfName("test"));
    }

    static class BeanNamesLocator extends TestBeanLocator {

        private final Class<?> type;
        private final List<String> beanNames;

        public BeanNamesLocator(Class<?> type, List<String> beanNames) {
            this.type = type;
            this.beanNames = beanNames;
        }

        @Override
        public List<String> getBeanNamesOfType(Class<?> aClass) {
            return type.equals(aClass) ? beanNames : List.of();
        }
    }

    static class BeanLocator extends TestBeanLocator {

        private final Class<?> type;
        private final Object bean;

        public BeanLocator(Class<?> type, Object bean) {
            this.type = type;
            this.bean = bean;
        }

        @Override
        public <T> T getBeanOfType(String s, Class<T> aClass) {
            return type.equals(aClass) ? (T) bean : null;
        }
    }

    static class BeansLocator extends TestBeanLocator {

        private final Class<?> type;
        private final List<Object> beans;

        public BeansLocator(Class<?> type, List<Object> beans) {
            this.type = type;
            this.beans = beans;
        }

        @Override
        public <T> Collection<? extends T> getBeansOfType(Class<T> aClass) {
            return type.equals(aClass) ? (Collection<? extends T>) beans : List.of();
        }
    }

    static class BeansLoader extends TestBeanLocator {
        BeanLoaderListener loadListener;

        @Override
        public <T> boolean loadBeansOfType(Class<T> aClass, BeanLoaderListener<T> beanLoaderListener) {
            this.loadListener = beanLoaderListener;
            return false;
        }
    }

    static class HasPropertyValue extends TestBeanLocator {

        private final String name;

        public HasPropertyValue(String name) {
            this.name = name;
        }

        @Override
        public boolean hasConfiguredPropertyValue(String s, String s1, String s2) {
            return s1.equals(name);
        }
    }

    static class HasBeanOfName extends TestBeanLocator {

        private final String name;

        public HasBeanOfName(String name) {
            this.name = name;
        }

        @Override
        public boolean hasBeanOfName(String s) {
            return s.equals(name);
        }
    }

    static class TestBeanLocator implements ConfiguredBeanLocator {

        @Override
        public List<String> getBeanNamesOfType(Class<?> aClass) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> T getBeanOfType(String s, Class<T> aClass) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> Collection<? extends T> getBeansOfType(Class<T> aClass) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> boolean loadBeansOfType(Class<T> aClass, BeanLoaderListener<T> beanLoaderListener) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean hasConfiguredPropertyValue(String s, String s1, String s2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean hasBeanOfName(String s) {
            throw new UnsupportedOperationException();
        }
    }
}
