package io.bootique.cxf.jaxws;

import io.bootique.cxf.conf.CustomConfigurer;
import org.apache.cxf.feature.Feature;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.message.Message;

import java.util.List;
import java.util.Set;

public class EndpointConfigurer implements CustomConfigurer<EndpointImpl> {

    private Set<Feature> features;
    private final Set<Interceptor<? extends Message>> inInterceptors;
    private final Set<Interceptor<? extends Message>> outInterceptors;
    private final Set<Interceptor<? extends Message>> inFaultInterceptors;
    private final Set<Interceptor<? extends Message>> outFaultInterceptors;

    public EndpointConfigurer(
            Set<Feature> features,
            Set<Interceptor<? extends Message>> inInterceptors,
            Set<Interceptor<? extends Message>> outInterceptors,
            Set<Interceptor<? extends Message>> inFaultInterceptors,
            Set<Interceptor<? extends Message>> outFaultInterceptors
            ) {
        this.features = features;
        this.inInterceptors = inInterceptors;
        this.outInterceptors = outInterceptors;
        this.inFaultInterceptors = inFaultInterceptors;
        this.outFaultInterceptors = outFaultInterceptors;
    }

    @Override
    public void configure(EndpointImpl instance) {

        instance.getFeatures().addAll(features);

        instance.getInInterceptors().addAll(inInterceptors);
        instance.getOutInterceptors().addAll(outInterceptors);
        instance.getInFaultInterceptors().addAll(inFaultInterceptors);
        instance.getOutFaultInterceptors().addAll(outFaultInterceptors);

    }
}
