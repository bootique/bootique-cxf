package io.bootique.cxf.conf;

import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import org.apache.cxf.configuration.Configurer;

import java.util.Map;

public class GuiceConfigurer implements Configurer {
    private final Injector injector;
    private final Map<TypeLiteral, CustomConfigurer> customConfigurers;

    public GuiceConfigurer(Injector injector, Map<TypeLiteral, CustomConfigurer> customConfigurers) {
        this.injector = injector;
        this.customConfigurers = customConfigurers;
    }

    @Override
    public void configureBean(Object beanInstance) {

        TypeLiteral typeLiteral = TypeLiteral.get(beanInstance.getClass());
        CustomConfigurer configurer = customConfigurers.get(typeLiteral);

        if (configurer != null) {
            configurer.configure(beanInstance);
        } else {
            injector.injectMembers(beanInstance);
        }
    }

    @Override
    public void configureBean(String name, Object beanInstance) {
        configureBean(beanInstance);
    }
}
