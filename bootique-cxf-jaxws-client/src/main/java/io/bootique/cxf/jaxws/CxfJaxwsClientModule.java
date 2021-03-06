package io.bootique.cxf.jaxws;

import io.bootique.ConfigModule;
import io.bootique.config.ConfigurationFactory;
import io.bootique.cxf.CxfModule;
import io.bootique.cxf.jaxws.annotation.CxfInterceptorsClientIn;
import io.bootique.cxf.jaxws.annotation.CxfInterceptorsClientInFault;
import io.bootique.cxf.jaxws.annotation.CxfInterceptorsClientOut;
import io.bootique.cxf.jaxws.annotation.CxfInterceptorsClientOutFault;
import io.bootique.di.Binder;
import io.bootique.di.Provides;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.message.Message;
import org.apache.cxf.transport.http.URLConnectionHTTPConduit;

import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import javax.inject.Singleton;

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
        CxfModule.extend(binder).addCustomConfigurer(JaxWsProxyFactoryBean.class, JaxWsProxyFactoryConfigurer.class, true);
        CxfModule.extend(binder).addCustomConfigurer(URLConnectionHTTPConduit.class, URLConnectionHTTPConduitConfigurer.class, true);
    }

    @Provides
    @Singleton
    public JaxWsProxyFactoryConfigurer provideProxyFactoryConfigurer(
            @CxfInterceptorsClientIn        Set<Interceptor<? extends Message>> inInterceptors,
            @CxfInterceptorsClientOut       Set<Interceptor<? extends Message>> outInterceptors,
            @CxfInterceptorsClientInFault   Set<Interceptor<? extends Message>> inFaultInterceptors,
            @CxfInterceptorsClientOutFault  Set<Interceptor<? extends Message>> outFaultInterceptors
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

    @Provides
    @NamedURLs
    public Map<String, URL> provideNamedURLs(CxfJaxwsClientConfiguration configuration) {
        return Collections.unmodifiableMap(configuration.urls);
    }

}
