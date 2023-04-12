package org.ryuu.rbean;

import org.ryuu.rbean.annotation.Bean;
import org.ryuu.rbean.annotation.Loading;
import org.ryuu.rbean.annotation.Scope;
import org.ryuu.rbean.util.PathUtils;

import java.beans.Introspector;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class AnnotationBeanFactory {
    private final ConcurrentHashMap<String, BeanDefinition> nameBeanDefinitionMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Object> singletonBeanMap = new ConcurrentHashMap<>();

    public AnnotationBeanFactory(String packageName) {
        Path packagePath = getPackagePath(packageName);
        ArrayList<Path> childrenPath = new ArrayList<>();
        try {
            PathUtils.getChildren(packagePath, childrenPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String firstPackageName = packageName.split("\\.")[0];
        for (Path path : childrenPath) {
            Class<?> klass = pathToClass(path, firstPackageName);
            if (!klass.isAnnotationPresent(Bean.class)) {
                continue;
            }
            String beanName = getBeanName(klass);
            BeanDefinition beanDefinition = createBeanDefinition(klass);
            nameBeanDefinitionMap.put(beanName, beanDefinition);
        }

        for (String name : nameBeanDefinitionMap.keySet()) {
            BeanDefinition definition = nameBeanDefinitionMap.get(name);
            if (
                    definition.getScopeType() == ScopeType.SINGLETON &&
                            definition.getLoadingStrategy() == LoadingStrategy.EAGER
            ) {
                Object bean = createBean(definition);
                singletonBeanMap.put(name, bean);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(String beanName, Class<T> klass) {
        BeanDefinition beanDefinition = nameBeanDefinitionMap.get(beanName);
        if (beanDefinition == null) {
            throw new IllegalStateException("Unexpected value: " + beanName);
        }

        Object bean;
        switch (beanDefinition.getScopeType()) {
            case SINGLETON:
                bean = singletonBeanMap.get(beanName);
                if (bean == null) {
                    bean = createBean(beanDefinition);
                    String name = getBeanName(beanDefinition.getType());
                    singletonBeanMap.put(name, bean);
                }
                break;
            case PROTOTYPE:
                bean = createBean(beanDefinition);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + beanDefinition.getScopeType());
        }
        if (bean.getClass().equals(klass)) {
            return (T) bean;
        }

        throw new IllegalStateException("Unexpected value: " + klass);
    }

    private static Class<?> pathToClass(Path path, String firstPackageName) {
        String pathString = PathUtils.toStringForwardSlash(path);
        String relativePathString = pathString.substring(pathString.lastIndexOf(firstPackageName));
        String className = relativePathString.replace('/', '.').substring(0, relativePathString.indexOf(".class"));
        try {
            return Thread.currentThread().getContextClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private String getBeanName(Class<?> klass) {
        Bean bean = klass.getAnnotation(Bean.class);
        String beanName = bean.name();
        if (beanName.equals("")) {
            beanName = Introspector.decapitalize(klass.getSimpleName());
        }
        return beanName;
    }

    private BeanDefinition createBeanDefinition(Class<?> klass) {
        BeanDefinition beanDefinition = new BeanDefinition();
        beanDefinition.setType(klass);
        beanDefinition.setScopeType(getScopeType(klass));
        beanDefinition.setLoadingStrategy(getLoadingStrategy(klass));
        return beanDefinition;
    }

    private ScopeType getScopeType(Class<?> klass) {
        if (!klass.isAnnotationPresent(Scope.class)) {
            return ScopeType.SINGLETON;
        }
        return klass.getAnnotation(Scope.class).scopeType();
    }

    private LoadingStrategy getLoadingStrategy(Class<?> klass) {
        if (!klass.isAnnotationPresent(Loading.class)) {
            return LoadingStrategy.EAGER;
        }
        return klass.getAnnotation(Loading.class).loadingStrategy();
    }

    private Object createBean(BeanDefinition definition) {
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

    private static Path getPackagePath(String packageName) {
        String packagePathString = packageName.replace('.', '/');
        URL url = Thread.currentThread().getContextClassLoader().getResource(packagePathString);
        return new File(Objects.requireNonNull(url).getFile()).toPath();
    }
}
