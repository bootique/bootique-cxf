package io.bootique.cxf.interceptor;


import com.google.inject.BindingAnnotation;
import io.bootique.cxf.CxfModuleExtender;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER, ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@BindingAnnotation
public @interface CxfInterceptors {

    enum Type {
        IN,
        OUT,
        IN_FAULT,
        OUT_FAULT;
    }

    String target() default CxfModuleExtender.BUS_INTERCEPTORS;
    Type type();

}
