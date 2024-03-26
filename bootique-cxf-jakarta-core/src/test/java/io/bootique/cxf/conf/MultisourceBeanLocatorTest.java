package io.bootique.cxf.conf;

import org.apache.cxf.configuration.ConfiguredBeanLocator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class MultisourceBeanLocatorTest {

    private ConfiguredBeanLocator source1;
    private ConfiguredBeanLocator source2;
    private MultisourceBeanLocator multisourceBeanLocator;

    @BeforeEach
    public void setUp() {
        source1 = mock(ConfiguredBeanLocator.class);
        source2 = mock(ConfiguredBeanLocator.class);

        multisourceBeanLocator = new MultisourceBeanLocator(source1, source2);
    }

    @Test
    public void atLeastOneSourceIsRequired() {
        assertThrows(Exception.class, () -> new MultisourceBeanLocator());
    }

    @Test
    public void getBeanNamesOfType_emptySource() {

        when(source1.getBeanNamesOfType(eq(Long.class))).thenReturn(emptyList());
        when(source2.getBeanNamesOfType(eq(Long.class))).thenReturn(asList("test", "test2"));

        List<String> names = multisourceBeanLocator.getBeanNamesOfType(Long.class);

        assertArrayEquals(asList("test", "test2").toArray(), names.toArray());
    }

    @Test
    public void getBeanNamesOfType_nullSource() {

        when(source1.getBeanNamesOfType(eq(Long.class))).thenReturn(null);
        when(source2.getBeanNamesOfType(eq(Long.class))).thenReturn(asList("test", "test2"));

        List<String> names = multisourceBeanLocator.getBeanNamesOfType(Long.class);

        assertArrayEquals(asList("test", "test2").toArray(), names.toArray());
    }

    @Test
    public void getBeanNamesOfType_mergeResults() {

        when(source1.getBeanNamesOfType(eq(Long.class))).thenReturn(asList("_test", "_test2"));
        when(source2.getBeanNamesOfType(eq(Long.class))).thenReturn(asList("test", "test2"));

        List<String> names = multisourceBeanLocator.getBeanNamesOfType(Long.class);

        assertArrayEquals(asList("_test", "_test2", "test", "test2").toArray(), names.toArray());
    }


    @Test
    public void getBeanOfType_nullFirstSource() {
        when(source1.getBeanOfType(anyString(), eq(Long.class))).thenReturn(null);
        when(source2.getBeanOfType(anyString(), eq(Long.class))).thenReturn(3L);

        Long bean = multisourceBeanLocator.getBeanOfType("test", Long.class);

        assertEquals(3L, bean.longValue());
    }


    @Test
    public void getBeanOfType_nullBothSources() {
        when(source1.getBeanOfType(anyString(), eq(Long.class))).thenReturn(null);
        when(source2.getBeanOfType(anyString(), eq(Long.class))).thenReturn(null);

        Long bean = multisourceBeanLocator.getBeanOfType("test", Long.class);

        assertNull(bean);
    }

    @Test
    public void getBeanOfType_notNullBothSources() {
        when(source1.getBeanOfType(anyString(), eq(Long.class))).thenReturn(2L);
        when(source2.getBeanOfType(anyString(), eq(Long.class))).thenReturn(3L);

        Long bean = multisourceBeanLocator.getBeanOfType("test", Long.class);

        verifyNoMoreInteractions(source2);
        assertEquals(2L, bean.longValue());
    }


    @Test
    public void getBeansOfType_emptySource() {


        when(source1.getBeansOfType(eq(Long.class))).thenReturn(emptyList());
        when(source2.getBeansOfType(eq(Long.class))).thenReturn((Collection) asList(2L, 3L));

        Collection<? extends Long> beans = multisourceBeanLocator.getBeansOfType(Long.class);

        assertArrayEquals(asList(2L, 3L).toArray(), beans.toArray());
    }

    @Test
    public void getBeansOfType_nullSource() {


        when(source1.getBeansOfType(eq(Long.class))).thenReturn(null);
        when(source2.getBeansOfType(eq(Long.class))).thenReturn((Collection) asList(2L, 3L));

        Collection<? extends Long> beans = multisourceBeanLocator.getBeansOfType(Long.class);

        assertArrayEquals(asList(2L, 3L).toArray(), beans.toArray());
    }


    @Test
    public void getBeansOfType_mergeSource() {
        when(source1.getBeansOfType(eq(Long.class))).thenReturn((Collection) asList(1L, 3L));
        when(source2.getBeansOfType(eq(Long.class))).thenReturn((Collection) asList(2L, 3L));

        Collection<? extends Long> beans = multisourceBeanLocator.getBeansOfType(Long.class);

        assertArrayEquals(asList(1L, 3L, 2L, 3L).toArray(), beans.toArray());
    }

    @Test
    public void loadBeansOfType() {

        ConfiguredBeanLocator.BeanLoaderListener listener = mock(ConfiguredBeanLocator.BeanLoaderListener.class);
        multisourceBeanLocator.loadBeansOfType(Long.class, listener);

        verify(source1).loadBeansOfType(eq(Long.class), eq(listener));
        verify(source2).loadBeansOfType(eq(Long.class), eq(listener));
    }

    @Test
    public void hasConfiguredPropertyValue_firstLocator() {
        when(source1.hasConfiguredPropertyValue(eq("test"), eq("testProp"), eq("val"))).thenReturn(true);
        when(source2.hasConfiguredPropertyValue(eq("test"), eq("testProp"), eq("val"))).thenReturn(false);

        verifyNoMoreInteractions(source2);
        assertTrue(multisourceBeanLocator.hasConfiguredPropertyValue("test", "testProp", "val"));
    }

    @Test
    public void hasConfiguredPropertyValue_secondLocator() {
        when(source1.hasConfiguredPropertyValue(eq("test"), eq("testProp"), eq("val"))).thenReturn(false);
        when(source2.hasConfiguredPropertyValue(eq("test"), eq("testProp"), eq("val"))).thenReturn(true);

        assertTrue(multisourceBeanLocator.hasConfiguredPropertyValue("test", "testProp", "val"));
    }

    @Test
    public void hasConfiguredPropertyValue_noLocator() {
        when(source1.hasConfiguredPropertyValue(eq("test"), eq("testProp"), eq("val"))).thenReturn(false);
        when(source2.hasConfiguredPropertyValue(eq("test"), eq("testProp"), eq("val"))).thenReturn(false);

        assertFalse(multisourceBeanLocator.hasConfiguredPropertyValue("test", "testProp", "val"));
    }


    @Test
    public void hasBeanOfName_firstLocator() {
        when(source1.hasBeanOfName(eq("test"))).thenReturn(true);
        when(source2.hasBeanOfName(eq("test"))).thenReturn(false);

        verifyNoMoreInteractions(source2);
        assertTrue(multisourceBeanLocator.hasBeanOfName("test"));
    }

    @Test
    public void hasBeanOfName_secondLocator() {
        when(source1.hasBeanOfName(eq("test"))).thenReturn(false);
        when(source2.hasBeanOfName(eq("test"))).thenReturn(true);

        assertTrue(multisourceBeanLocator.hasBeanOfName("test"));
    }

    @Test
    public void hasBeanOfName_noLocator() {
        when(source1.hasBeanOfName(eq("test"))).thenReturn(false);
        when(source2.hasBeanOfName(eq("test"))).thenReturn(false);

        assertFalse(multisourceBeanLocator.hasBeanOfName("test"));
    }
}
