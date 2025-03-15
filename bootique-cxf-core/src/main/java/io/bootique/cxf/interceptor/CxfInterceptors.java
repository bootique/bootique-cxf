package io.bootique.cxf.interceptor;

import jakarta.inject.Qualifier;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @deprecated since 2.0, use one of {@link CxfInterceptorsIn}, {@link CxfInterceptorsOut}, {@link CxfInterceptorsInFault}
 *             or {@link CxfInterceptorsOutFault} instead.
 */
@Target({ElementType.PARAMETER, ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Qualifier
@Deprecated(since = "3.0", forRemoval = true)
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
