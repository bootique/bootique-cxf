package io.bootique.cxf.jaxws;

import io.bootique.cxf.conf.CustomConfigurer;
import io.bootique.di.Injector;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.message.Message;

import java.util.Set;

public class EndpointConfigurer implements CustomConfigurer<EndpointImpl> {

    private final Set<Interceptor<? extends Message>> inInterceptors;
    private final Set<Interceptor<? extends Message>> outInterceptors;
    private final Set<Interceptor<? extends Message>> inFaultInterceptors;
    private final Set<Interceptor<? extends Message>> outFaultInterceptors;
    private final Injector injector;

    public EndpointConfigurer(
            Set<Interceptor<? extends Message>> inInterceptors,
            Set<Interceptor<? extends Message>> outInterceptors,
            Set<Interceptor<? extends Message>> inFaultInterceptors,
            Set<Interceptor<? extends Message>> outFaultInterceptors,
            Injector injector
            ) {
        this.inInterceptors = inInterceptors;
        this.outInterceptors = outInterceptors;
        this.inFaultInterceptors = inFaultInterceptors;
        this.outFaultInterceptors = outFaultInterceptors;
        this.injector = injector;
    }

    @Override
    public void configure(EndpointImpl instance) {


        instance.getInInterceptors().addAll(inInterceptors);
        instance.getOutInterceptors().addAll(outInterceptors);
        instance.getInFaultInterceptors().addAll(inFaultInterceptors);
        instance.getOutFaultInterceptors().addAll(outFaultInterceptors);

        if (instance.getImplementor() != null) {
            injector.injectMembers(instance.getImplementor());
        }
    }
}
