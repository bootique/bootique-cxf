package io.bootique.cxf.conf;

import org.apache.cxf.configuration.ConfiguredBeanLocator;

import java.util.*;
import java.util.stream.Collectors;

public class MultisourceBeanLocator implements ConfiguredBeanLocator {

    private List<ConfiguredBeanLocator> locators;

    public MultisourceBeanLocator(ConfiguredBeanLocator... locators) {
        if (locators.length < 1) {
            throw new IllegalArgumentException("At least one locator is required");
        }
        this.locators = Arrays.asList(locators);
    }

    @Override
    public List<String> getBeanNamesOfType(Class<?> type) {

        return locators.stream()
                .map(beanLocator -> getBeanNamesOfType(type))
                .filter(Objects::nonNull)
                .collect(ArrayList::new, List::addAll, List::addAll);
    }

    @Override
    public <T> T getBeanOfType(String name, Class<T> type) {
        return locators.stream()
                .map(locator -> locator.getBeanOfType(name, type))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

    }

    @Override
    public <T> Collection<? extends T> getBeansOfType(Class<T> type) {
        return locators.stream()
                .map(beanLocator -> beanLocator.<T>getBeansOfType(type))
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    @Override
    public <T> boolean loadBeansOfType(Class<T> type, BeanLoaderListener<T> listener) {
        return locators.stream()
                .map(locator -> locator.loadBeansOfType(type, listener))
                .filter(Boolean::booleanValue)
                .findFirst().orElse(false);
    }

    @Override
    public boolean hasConfiguredPropertyValue(String beanName, String propertyName, String value) {
        return locators.stream()
                .map(locator -> locator.hasConfiguredPropertyValue(beanName, propertyName, value))
                .filter(Boolean::booleanValue)
                .findFirst().orElse(false);
    }

    @Override
    public boolean hasBeanOfName(String name) {
        return locators.stream()
                .map(locator -> locator.hasBeanOfName(name))
                .filter(Boolean::booleanValue)
                .findFirst().orElse(false);
    }
}
