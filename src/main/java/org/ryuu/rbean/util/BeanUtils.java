package org.ryuu.rbean.util;

import org.ryuu.rbean.BeanDefinition;
import org.ryuu.rbean.LoadingStrategy;
import org.ryuu.rbean.ScopeType;
import org.ryuu.rbean.annotation.Bean;
import org.ryuu.rbean.annotation.Loading;
import org.ryuu.rbean.annotation.Scope;

import java.beans.Introspector;
import java.lang.reflect.InvocationTargetException;

public class BeanUtils {
    private BeanUtils() {
    }

    public static BeanDefinition createBeanDefinition(Class<?> type) {
        BeanDefinition beanDefinition = new BeanDefinition();
        beanDefinition.setType(type);
        beanDefinition.setScopeType(getScopeType(type));
        beanDefinition.setLoadingStrategy(getLoadingStrategy(type));
        return beanDefinition;
    }

    public static Object createBean(BeanDefinition definition) {
        Class<?> type = definition.getType();
        try {
            return type.getConstructor().newInstance();
        } catch (
                InstantiationException |
                IllegalAccessException |
                InvocationTargetException |
                NoSuchMethodException e
        ) {
            throw new RuntimeException(e);
        }
    }

    public static String getBeanName(Class<?> type) {
        Bean bean = type.getAnnotation(Bean.class);
        String beanName = bean.name();
        if (beanName.equals(Bean.DEFAULT_NAME)) {
            beanName = BeanUtils.getDefaultBeanName(type);
        }
        return beanName;
    }

    public static ScopeType getScopeType(Class<?> type) {
        if (!type.isAnnotationPresent(Scope.class)) {
            return ScopeType.SINGLETON;
        }
        return type.getAnnotation(Scope.class).scopeType();
    }

    public static LoadingStrategy getLoadingStrategy(Class<?> type) {
        if (!type.isAnnotationPresent(Loading.class)) {
            return LoadingStrategy.EAGER;
        }
        return type.getAnnotation(Loading.class).loadingStrategy();
    }

    private static String getDefaultBeanName(Class<?> type) {
        return Introspector.decapitalize(type.getSimpleName());
    }
}
