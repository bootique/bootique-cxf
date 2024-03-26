package io.bootique.cxf.conf;

import io.bootique.di.Injector;
import io.bootique.di.TypeLiteral;
import org.apache.cxf.configuration.Configurer;

import java.util.List;
import java.util.Map;

public class BqConfigurer implements Configurer {
    private final Injector injector;
    private final Map<TypeLiteral<?>, List<CustomConfigurer<?>>> customConfigurers;

    public BqConfigurer(Injector injector, Map<TypeLiteral<?>, List<CustomConfigurer<?>>> customConfigurers) {
        this.injector = injector;
        this.customConfigurers = customConfigurers;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void configureBean(Object beanInstance) {

        TypeLiteral<?> typeLiteral = TypeLiteral.of(beanInstance.getClass());
        List<CustomConfigurer<?>> configurers = customConfigurers.get(typeLiteral);

        if (configurers != null && !configurers.isEmpty()) {
            configurers.forEach(configurer -> ((CustomConfigurer)configurer).configure(beanInstance));
        } else {
            injector.injectMembers(beanInstance);
        }
    }

    @Override
    public void configureBean(String name, Object beanInstance) {
        configureBean(beanInstance);
    }
}
