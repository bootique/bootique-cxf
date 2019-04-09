package io.bootique.cxf.jaxws;

import com.google.inject.Binder;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import io.bootique.ConfigModule;
import io.bootique.config.ConfigurationFactory;
import io.bootique.cxf.CxfModule;
import io.bootique.cxf.interceptor.CxfInterceptors;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.message.Message;
import org.apache.cxf.transport.http.URLConnectionHTTPConduit;

import java.util.Set;

import static io.bootique.cxf.jaxws.CxfJaxwsClientModuleExtender.JAX_WS_CLIENT_INTERCEPTORS;

public class CxfJaxwsClientModule extends ConfigModule {

    public static final String CONF_PREFIX = "cxfjaxwsclient";

    public static CxfJaxwsClientModuleExtender extend(Binder binder) {
        return new CxfJaxwsClientModuleExtender(binder);
    }

    public CxfJaxwsClientModule() {
        super(CONF_PREFIX);
    }

    @Override
    public void configure(Binder binder) {

        CxfJaxwsClientModule.extend(binder).initAllExtensions();
        CxfModule.extend(binder).addCustomConfigurer(JaxWsProxyFactoryBean.class, JaxWsProxyFactoryConfigurer.class);
        CxfModule.extend(binder).addCustomConfigurer(URLConnectionHTTPConduit.class, URLConnectionHTTPConduitConfigurer.class);

    }

    @Provides
    @Singleton
    public JaxWsProxyFactoryConfigurer provideProxyFactoryConfigurer(
            @CxfInterceptors(target = JAX_WS_CLIENT_INTERCEPTORS, type = CxfInterceptors.Type.IN) Set<Interceptor<? extends Message>> inInterceptors,
            @CxfInterceptors(target = JAX_WS_CLIENT_INTERCEPTORS, type = CxfInterceptors.Type.OUT) Set<Interceptor<? extends Message>> outInterceptors,
            @CxfInterceptors(target = JAX_WS_CLIENT_INTERCEPTORS, type = CxfInterceptors.Type.IN_FAULT) Set<Interceptor<? extends Message>> inFaultInterceptors,
            @CxfInterceptors(target = JAX_WS_CLIENT_INTERCEPTORS, type = CxfInterceptors.Type.OUT_FAULT) Set<Interceptor<? extends Message>> outFaultInterceptors
    ) {


        return new JaxWsProxyFactoryConfigurer(
                inInterceptors,
                outInterceptors,
                inFaultInterceptors,
                outFaultInterceptors
        );
    }


    @Provides
    @Singleton
    public CxfJaxwsClientConfiguration provideConfiguration(ConfigurationFactory configurationFactory) {
        return configurationFactory.config(CxfJaxwsClientConfiguration.class, CONF_PREFIX);
    }

    @Provides
    @Singleton
    public URLConnectionHTTPConduitConfigurer httpConduitConfigurer(CxfJaxwsClientConfiguration configuration) {
        return new URLConnectionHTTPConduitConfigurer(configuration.followRedirects, configuration.readTimeoutMs, configuration.connectTimeoutMs);
    }

}
