package io.bootique.cxf.jaxws;

import io.bootique.cxf.conf.CustomConfigurer;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.message.Message;

import java.util.Set;

/**
 * @deprecated The users are encouraged to switch to the Jakarta-based flavor
 */
@Deprecated(since = "3.0", forRemoval = true)
public class JaxWsProxyFactoryConfigurer implements CustomConfigurer<JaxWsProxyFactoryBean> {

    private final Set<Interceptor<? extends Message>> inInterceptors;
    private final Set<Interceptor<? extends Message>> outInterceptors;
    private final Set<Interceptor<? extends Message>> inFaultInterceptors;
    private final Set<Interceptor<? extends Message>> outFaultInterceptors;

    public JaxWsProxyFactoryConfigurer(
            Set<Interceptor<? extends Message>> inInterceptors,
            Set<Interceptor<? extends Message>> outInterceptors,
            Set<Interceptor<? extends Message>> inFaultInterceptors,
            Set<Interceptor<? extends Message>> outFaultInterceptors
    ) {
        this.inInterceptors = inInterceptors;
        this.outInterceptors = outInterceptors;
        this.inFaultInterceptors = inFaultInterceptors;
        this.outFaultInterceptors = outFaultInterceptors;
    }

    @Override
    public void configure(JaxWsProxyFactoryBean proxyFactoryBean) {
        proxyFactoryBean.getInInterceptors().addAll(inInterceptors);
        proxyFactoryBean.getOutInterceptors().addAll(outInterceptors);
        proxyFactoryBean.getInFaultInterceptors().addAll(inFaultInterceptors);
        proxyFactoryBean.getOutFaultInterceptors().addAll(outFaultInterceptors);
    }
}
