package org.ryuu.bean.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.ryuu.bean.Bean;
import org.ryuu.bean.BeanDefinition;
import org.ryuu.bean.LoadingStrategy;
import org.ryuu.bean.ScopeType;
import org.ryuu.bean.factory.BeanFactory;
import org.ryuu.bean.string.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BeanUtils {
    public static Object createBean(BeanDefinition definition, BeanFactory beanFactory) {
        Method method = definition.getMethod();
        if (method == null) {
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
        } else {
            Object parentBean = beanFactory.getBean(definition.getParentName(), Object.class);
            try {
                return method.invoke(parentBean);
            } catch (
                    IllegalAccessException |
                    InvocationTargetException e
            ) {
                throw new RuntimeException("Create bean failed.", e);
            }
        }
    }

    public static String getDefaultBeanName(Class<?> type) {
        return StringUtils.decapitalize(type.getSimpleName());
    }

    public static List<BeanDefinition> createBeanDefinitions(Class<?> type) {
        if (type == null) {
            return Collections.emptyList();
        }

        List<BeanDefinition> beanDefinitions = new ArrayList<>();
        BeanDefinition typeBeanDefinition = TypeBean.createBeanDefinition(type);
        if (typeBeanDefinition != null) {
            beanDefinitions.add(typeBeanDefinition);
        }
        List<BeanDefinition> methodBeanDefinitions = MethodBean.createBeanDefinitions(type);
        beanDefinitions.addAll(methodBeanDefinitions);
        return beanDefinitions;
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class TypeBean {
        public static String getBeanName(Class<?> type) {
            Bean bean = type.getAnnotation(Bean.class);
            if (bean == null) {
                return null;
            }
            String beanName = bean.name();

            if (beanName.isEmpty()) {
                beanName = BeanUtils.getDefaultBeanName(type);
            }
            return beanName;
        }

        private static BeanDefinition createBeanDefinition(Class<?> type) {
            if (!isBean(type)) {
                return null;
            }

            return BeanDefinition
                    .builder()
                    .name(getBeanName(type))
                    .type(type)
                    .scopeType(getScopeType(type))
                    .loadingStrategy(getLoadingStrategy(type))
                    .dependencies(Arrays.asList(getDependencies(type)))
                    .build();
        }

        private static boolean isBean(Class<?> type) {
            return type.isAnnotationPresent(Bean.class);
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

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class MethodBean {
        public static String getBeanName(Method method) {
            String beanName = method.getAnnotation(Bean.class).name();
            if (beanName.isEmpty()) {
                beanName = method.getName();
            }

            return beanName;
        }

        private static List<BeanDefinition> createBeanDefinitions(Class<?> type) {
            ArrayList<BeanDefinition> beanDefinitions = new ArrayList<>();
            Method[] methods = type.getMethods();
            for (Method method : methods) {
                if (!isBean(method)) {
                    continue;
                }

                Class<?> returnType = method.getReturnType();
                if (returnType == void.class) {
                    System.err.println("The return value of a bean method cannot be void.");
                    continue;
                }
                BeanDefinition beanDefinition = BeanDefinition
                        .builder()
                        .name(getBeanName(method))
                        .parentName(TypeBean.getBeanName(type))
                        .method(method)
                        .type(returnType)
                        .scopeType(getScopeType(method))
                        .loadingStrategy(getLoadingStrategy(method))
                        .dependencies(Arrays.asList(getDependencies(method)))
                        .build();
                beanDefinitions.add(beanDefinition);
            }
            return beanDefinitions;
        }

        private static boolean isBean(Method method) {
            return method.isAnnotationPresent(Bean.class);
        }

        private static ScopeType getScopeType(Method method) {
            if (!method.isAnnotationPresent(Bean.Scope.class)) {
                return ScopeType.SINGLETON;
            }
            return method.getAnnotation(Bean.Scope.class).scopeType();
        }

        private static LoadingStrategy getLoadingStrategy(Method method) {
            if (!method.isAnnotationPresent(Bean.Loading.class)) {
                return LoadingStrategy.EAGER;
            }
            return method.getAnnotation(Bean.Loading.class).loadingStrategy();
        }

        private static String[] getDependencies(Method method) {
            if (!method.isAnnotationPresent(Bean.DependOn.class)) {
                return new String[0];
            }
            return method.getAnnotation(Bean.DependOn.class).dependencies();
        }
    }
}
