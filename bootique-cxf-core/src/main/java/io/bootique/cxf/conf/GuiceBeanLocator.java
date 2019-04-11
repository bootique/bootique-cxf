package io.bootique.cxf.conf;

import com.google.inject.*;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import org.apache.cxf.configuration.ConfiguredBeanLocator;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * Adapter for the Guice injector. Delivers the configured beans from the Guice context.
 * Since the "name" attribute is not mandatory for the Guice managed beans, tries to use the {@link Named} annotation.
 * Otherwise, will accept class name as a "name".
 */
public class GuiceBeanLocator implements ConfiguredBeanLocator {

    private final Injector injector;

    public GuiceBeanLocator(Injector injector) {
        this.injector = injector;
    }

    @Override
    public List<String> getBeanNamesOfType(Class<?> type) {
        return injector.findBindingsByType(TypeLiteral.get(type)).stream()
                .map(Binding::getKey)
                .map(this::getNameFromKey)
                .collect(Collectors.toList());


    }

    @Override
    public <T> T getBeanOfType(String name, Class<T> type) {

        // if this is not a call for a named bean
        if (name == null || name.equals(type.getName())) {
            return tryGetInstance(Key.get(type)).orElse(null);
        } else {
            return tryGetInstance(Key.get(type, Names.named(name))).orElse(null);
        }

    }

    @Override
    public <T> Collection<? extends T> getBeansOfType(Class<T> type) {

        return injector.findBindingsByType(TypeLiteral.get(type)).stream()
                .map(Binding::getProvider)
                .map(Provider::get)
                .collect(Collectors.toList());


    }

    @Override
    public <T> boolean loadBeansOfType(Class<T> type, BeanLoaderListener<T> listener) {

        // not supporting that in the Guice managed beans
        return false;
    }

    @Override
    public boolean hasConfiguredPropertyValue(String beanName, String propertyName, String value) {
        // not supporting that in the Guice managed beans

        return false;
    }

    @Override
    public boolean hasBeanOfName(String name) {

        // supporting only beans named like the class

        return tryLoadClassByName(name)
                .map(type -> !getBeansOfType(type).isEmpty())
                .orElse(false);

    }


    // checking if this is the named key
    // otherwise return class name

    private String getNameFromKey(Key<?> key) {
        return Optional.ofNullable(key.getAnnotation())
                .filter(annotation -> annotation instanceof Named)
                .map(annotation -> ((Named) annotation).value())
                .orElse(key.getTypeLiteral().toString());

    }

    // safely trying to get the configured instance
    // returning null otherwise
    private <T> Optional<T> tryGetInstance(Key<T> key) {
        return Optional.ofNullable(injector.getExistingBinding(key))
                .map(Binding::getProvider)
                .map(Provider::get);
    }

    private Optional<Class> tryLoadClassByName(String name) {
        try {
            return Optional.of(Class.forName(name));
        } catch (ClassNotFoundException e) {
            return Optional.empty();
        }
    }

}
