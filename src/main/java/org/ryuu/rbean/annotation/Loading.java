package org.ryuu.rbean.annotation;

import org.ryuu.rbean.LoadingStrategy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Loading {
    LoadingStrategy loadingStrategy();
}
