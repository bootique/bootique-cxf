package io.bootique.cxf.jaxws;

import io.bootique.BQModule;
import io.bootique.ModuleCrate;
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
import org.apache.cxf.transport.http.HttpClientHTTPConduit;

import javax.inject.Singleton;
import java.net.URL;
import java.util.Map;
import java.util.Set;

/**
 * @deprecated The users are encouraged to switch to the Jakarta-based flavor
 */
@Deprecated(since = "3.0", forRemoval = true)
public class CxfJaxwsClientModule implements BQModule {

    private static final String CONFIG_PREFIX = "cxfjaxwsclient";

    public static CxfJaxwsClientModuleExtender extend(Binder binder) {
        return new CxfJaxwsClientModuleExtender(binder);
    }

    @Override
    public ModuleCrate crate() {
        return ModuleCrate.of(this)
                .description("Deprecated, can be replaced with 'bootique-cxf-jakarta-jaxws-client'.")
                .config(CONFIG_PREFIX, CxfJaxwsClientFactory.class)
                .build();
    }

    @Override
    public void configure(Binder binder) {
        CxfJaxwsClientModule.extend(binder).initAllExtensions();
        CxfModule.extend(binder).addCustomConfigurer(JaxWsProxyFactoryBean.class, JaxWsProxyFactoryConfigurer.class, true);
        CxfModule.extend(binder).addCustomConfigurer(HttpClientHTTPConduit.class, HttpClientHTTPConduitConfigurer.class, true);
    }

    @Provides
    @Singleton
    public JaxWsProxyFactoryConfigurer provideProxyFactoryConfigurer(
            @CxfInterceptorsClientIn Set<Interceptor<? extends Message>> inInterceptors,
            @CxfInterceptorsClientOut Set<Interceptor<? extends Message>> outInterceptors,
            @CxfInterceptorsClientInFault Set<Interceptor<? extends Message>> inFaultInterceptors,
            @CxfInterceptorsClientOutFault Set<Interceptor<? extends Message>> outFaultInterceptors
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
    public HttpClientHTTPConduitConfigurer httpConduitConfigurer(ConfigurationFactory configFactory) {
        return configFactory.config(CxfJaxwsClientFactory.class, CONFIG_PREFIX).createConfigurer();
    }

    @Provides
    @NamedURLs
    public Map<String, URL> provideNamedURLs(ConfigurationFactory configFactory) {
        return configFactory.config(CxfJaxwsClientFactory.class, CONFIG_PREFIX).getUrls();
    }
}
