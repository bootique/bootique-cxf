package io.bootique.cxf.conf;

import io.bootique.di.Injector;
import io.bootique.di.Key;
import org.apache.cxf.configuration.ConfiguredBeanLocator;

import jakarta.inject.Named;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adapter for the Bootique Injector. Delivers the configured beans from the BQ context.
 * Since the "name" attribute is not mandatory for the Bootique managed beans, tries to use the {@link Named} annotation.
 * Otherwise, will accept class name as a "name".
 */
public class BQBeanLocator implements ConfiguredBeanLocator {

    private final Injector injector;

    public BQBeanLocator(Injector injector) {
        this.injector = injector;
    }

    @Override
    public List<String> getBeanNamesOfType(Class<?> type) {
        Collection<? extends Key<?>> keysByType = injector.getKeysByType(type);
        return keysByType.stream()
                .map(this::getNameFromKey)
                .collect(Collectors.toList());
    }

    @Override
    public <T> T getBeanOfType(String name, Class<T> type) {

        // if this is not a call for a named bean
        if (name == null || name.equals(type.getName())) {
            return tryGetInstance(Key.get(type)).orElse(null);
        } else {
            return tryGetInstance(Key.get(type, name)).orElse(null);
        }

    }

    @Override
    public <T> Collection<? extends T> getBeansOfType(Class<T> type) {
        Collection<Key<T>> keysByType = injector.getKeysByType(type);
        return keysByType.stream()
                .map(injector::getInstance)
                .collect(Collectors.toList());
    }

    @Override
    public <T> boolean loadBeansOfType(Class<T> type, BeanLoaderListener<T> listener) {
        // not supporting that in the Bootique managed beans
        return false;
    }

    @Override
    public boolean hasConfiguredPropertyValue(String beanName, String propertyName, String value) {
        // not supporting that in the Bootique managed beans
        return false;
    }

    @Override
    public boolean hasBeanOfName(String name) {
        // supporting only beans named like the class
        return tryLoadClassByName(name)
                .map(type -> !getBeansOfType(type).isEmpty())
                .orElse(false);
    }

    /**
     * checking if this is the named key
     * otherwise return class name
     */
    private String getNameFromKey(Key<?> key) {
        return Optional.ofNullable(key.getBindingName())
                .orElse(key.getType().toString());
    }

    // safely trying to get the configured instance
    // returning null otherwise
    private <T> Optional<T> tryGetInstance(Key<T> key) {
        if(injector.hasProvider(key)) {
            return Optional.of(injector.getProvider(key).get());
        } else {
            return Optional.empty();
        }
    }

    private Optional<Class<?>> tryLoadClassByName(String name) {
        try {
            return Optional.of(Class.forName(name));
        } catch (ClassNotFoundException e) {
            return Optional.empty();
        }
    }

}
