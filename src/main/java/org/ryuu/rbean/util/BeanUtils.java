package org.ryuu.rbean.util;

import org.ryuu.rbean.BeanDefinition;
import org.ryuu.rbean.LoadingStrategy;
import org.ryuu.rbean.ScopeType;
import org.ryuu.rbean.Bean;

import java.beans.Introspector;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class BeanUtils {
    private BeanUtils() {
    }

    public static BeanDefinition createBeanDefinition(Class<?> type) {
        BeanDefinition beanDefinition = new BeanDefinition();
        beanDefinition.setType(type);
        beanDefinition.setScopeType(getScopeType(type));
        beanDefinition.setLoadingStrategy(getLoadingStrategy(type));
        beanDefinition.setDependencies(Arrays.asList(getDependencies(type)));
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
            throw new RuntimeException("Create bean failed.", e);
        }
    }

    public static String getBeanName(Class<?> type) {
        Bean bean = type.getAnnotation(Bean.class);
        String beanName;
        if (bean == null) {
            beanName = "";
        } else {
            beanName = bean.name();
        }
        if (beanName.equals(Bean.DEFAULT_NAME)) {
            beanName = BeanUtils.getDefaultBeanName(type);
        }
        return beanName;
    }

    public static String getDefaultBeanName(Class<?> type) {
        return Introspector.decapitalize(type.getSimpleName());
    }

    private static ScopeType getScopeType(Class<?> type) {
        if (!type.isAnnotationPresent(Bean.Scope.class)) {
            return ScopeType.SINGLETON;
        }
        return type.getAnnotation(Bean.Scope.class).scopeType();
    }

    private static LoadingStrategy getLoadingStrategy(Class<?> type) {
        if (!type.isAnnotationPresent(Bean.Loading.class)) {
            return LoadingStrategy.EAGER;
        }
        return type.getAnnotation(Bean.Loading.class).loadingStrategy();
    }

    private static String[] getDependencies(Class<?> type) {
        if (!type.isAnnotationPresent(Bean.DependOn.class)) {
            return new String[0];
        }
        return type.getAnnotation(Bean.DependOn.class).dependencies();
    }
}
