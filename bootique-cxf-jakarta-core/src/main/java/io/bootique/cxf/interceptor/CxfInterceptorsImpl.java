package io.bootique.cxf.interceptor;

import java.lang.annotation.Annotation;
import java.util.Objects;

@Deprecated
class CxfInterceptorsImpl implements CxfInterceptors {
    private final String target;
    private final Type type;

    CxfInterceptorsImpl(String target, Type type) {
        this.target = Objects.requireNonNull(target);
        this.type = Objects.requireNonNull(type);
    }

    @Override
    public String target() {
        return this.target;
    }

    @Override
    public Type type() {
        return this.type;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return CxfInterceptors.class;
    }

    @Override
    public boolean equals(Object o) {
        // This is specified in java.lang.Annotation.
        if (!(o instanceof CxfInterceptors)) {
            return false;
        }

        CxfInterceptors other = (CxfInterceptors) o;

        return target.equals(other.target()) &&
                type == other.type();
    }

    @Override
    public int hashCode() {
        // This is specified in java.lang.Annotation.
        return ((127 * "target".hashCode()) ^ target.hashCode()) + ((127 * "type".hashCode()) ^ type.hashCode());
    }
}
