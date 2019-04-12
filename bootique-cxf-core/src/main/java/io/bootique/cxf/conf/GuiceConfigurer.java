package io.bootique.cxf.conf;

import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import org.apache.cxf.configuration.Configurer;

import java.util.List;
import java.util.Map;

public class GuiceConfigurer implements Configurer {
    private final Injector injector;
    private final Map<TypeLiteral, List<CustomConfigurer>> customConfigurers;

    public GuiceConfigurer(Injector injector, Map<TypeLiteral, List<CustomConfigurer>> customConfigurers) {
        this.injector = injector;
        this.customConfigurers = customConfigurers;
    }

    @Override
    public void configureBean(Object beanInstance) {

        TypeLiteral typeLiteral = TypeLiteral.get(beanInstance.getClass());
        List<CustomConfigurer> configurers = customConfigurers.get(typeLiteral);

        if (configurers != null && !configurers.isEmpty()) {
            configurers.forEach(configurer -> configurer.configure(beanInstance));
        } else {
            injector.injectMembers(beanInstance);
        }
    }

    @Override
    public void configureBean(String name, Object beanInstance) {
        configureBean(beanInstance);
    }
}
