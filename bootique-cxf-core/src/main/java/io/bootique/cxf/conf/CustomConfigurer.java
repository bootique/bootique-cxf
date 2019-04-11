package io.bootique.cxf.conf;

public interface CustomConfigurer<T> {

    void configure(T instance);
}
