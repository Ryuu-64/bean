package org.ryuu.bean.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.ryuu.bean.*;
import org.ryuu.bean.factory.BeanFactory;
import org.ryuu.bean.string.util.StringUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Beans {
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class TypeBeans {
        public static Optional<String> getBeanName(Class<?> type) {
            Bean bean = type.getAnnotation(Bean.class);
            if (bean == null) {
                return Optional.empty();
            }

            String beanName = bean.name();

            if (beanName.isEmpty()) {
                beanName = Beans.getDefaultBeanName(type);
            }

            return Optional.ofNullable(beanName);
        }

        private static Optional<BeanDefinition> createBeanDefinition(Class<?> type) {
            if (!isBean(type)) {
                return Optional.empty();
            }

            Optional<String> name = getBeanName(type);
            if (!name.isPresent()) {
                return Optional.empty();
            }

            BeanDefinition definition = BeanDefinition
                    .builder()
                    .name(name.get())
                    .type(type)
                    .scopeType(getScopeType(type))
                    .loadingStrategy(getLoadingStrategy(type))
                    .dependencies(Arrays.asList(getDependencies(type)))
                    .build();

            return Optional.ofNullable(definition);
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
    private static class MethodBeans {
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

                Optional<String> parentName = TypeBeans.getBeanName(type);
                if (!parentName.isPresent()) {
                    System.err.println("The parent name of a bean method cannot be null. This usually happen when the method bean's class is not a bean. type=" + type);
                    continue;
                }

                BeanDefinition beanDefinition = BeanDefinition
                        .builder()
                        .name(getBeanName(method))
                        .parentName(parentName.get())
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

    public static Object create(BeanDefinition definition, BeanFactory factory) {
        Method method = definition.getMethod();
        if (method == null) {
            return instantiateByConstructor(definition, factory);
        }

        return instantiateByMethod(definition, factory, method);
    }

    public static String getDefaultBeanName(Class<?> type) {
        return StringUtils.decapitalize(type.getSimpleName());
    }

    public static List<BeanDefinition> createBeanDefinitions(Class<?> type) {
        if (type == null) {
            return Collections.emptyList();
        }

        List<BeanDefinition> beanDefinitions = new ArrayList<>();

        Optional<BeanDefinition> typeBeanDefinition = TypeBeans.createBeanDefinition(type);
        typeBeanDefinition.ifPresent(beanDefinitions::add);

        List<BeanDefinition> methodBeanDefinitions = MethodBeans.createBeanDefinitions(type);
        beanDefinitions.addAll(methodBeanDefinitions);

        return beanDefinitions;
    }

    private static Object instantiateByMethod(BeanDefinition definition, BeanFactory factory, Method method) {
        Object parentBean = factory.getBean(definition.getParentName(), Object.class);
        try {
            return method.invoke(parentBean);
        } catch (
                IllegalAccessException |
                InvocationTargetException e
        ) {
            throw new RuntimeException("Create bean failed.", e);
        }
    }

    private static Object instantiateByConstructor(BeanDefinition definition, BeanFactory factory) {
        Class<?> type = definition.getType();
        Constructor<?>[] constructors = type.getDeclaredConstructors();
        for (Constructor<?> constructor : constructors) {
            try {
                return instantiateByDeclared(factory, constructor);
            } catch (InvocationTargetException | IllegalAccessException | InstantiationException e) {
                throw new RuntimeException("Create bean failed.", e);
            }
        }
        try {
            return instantiateByDefault(type);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            throw new RuntimeException("Create bean failed.", e);
        }
    }

    private static Object instantiateByDeclared(BeanFactory factory, Constructor<?> constructor) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        Parameter[] parameters = constructor.getParameters();
        Object[] constructorArgs = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            constructorArgs[i] = getConstructorArgBean(factory, parameter);
        }
        return constructor.newInstance(constructorArgs);
    }

    private static Object instantiateByDefault(Class<?> type) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return type.getConstructor().newInstance();
    }

    private static Object getConstructorArgBean(BeanFactory factory, Parameter parameter) {
        Class<?> type = parameter.getType();
        if (!parameter.isAnnotationPresent(Qualifier.class)) {
            return factory.getBean(type);
        }

        String name = parameter.getAnnotation(Qualifier.class).value();
        return factory.getBean(name, type);
    }
}
