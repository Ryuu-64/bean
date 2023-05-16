package org.ryuu.bean.util;

import org.ryuu.bean.BeanDefinition;
import org.ryuu.bean.LoadingStrategy;
import org.ryuu.bean.ScopeType;
import org.ryuu.bean.Bean;
import org.ryuu.bean.string.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class BeanUtils {
    private BeanUtils() {
    }

    public static BeanDefinition createBeanDefinition(Class<?> type) {
        return BeanDefinition
                .builder()
                .type(type)
                .scopeType(getScopeType(type))
                .loadingStrategy(getLoadingStrategy(type))
                .dependencies(Arrays.asList(getDependencies(type)))
                .build();
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

    /**
     * Unable to use {@link java.beans.Introspector#decapitalize(String)}, as it is not available in Android.
     */
    public static String getDefaultBeanName(Class<?> type) {
        return StringUtils.decapitalize(type.getSimpleName());
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
