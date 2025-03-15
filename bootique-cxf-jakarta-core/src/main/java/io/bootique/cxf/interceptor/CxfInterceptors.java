package io.bootique.cxf.interceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import jakarta.inject.Qualifier;

/**
 * @deprecated since 2.0, use one of {@link CxfInterceptorsIn}, {@link CxfInterceptorsOut}, {@link CxfInterceptorsInFault}
 *             or {@link CxfInterceptorsOutFault} instead.
 */
@Target({ElementType.PARAMETER, ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Qualifier
@Deprecated
public @interface CxfInterceptors {

    enum Type {
        IN,
        OUT,
        IN_FAULT,
        OUT_FAULT;
    }

    String target() default "bus";
    Type type();

}
