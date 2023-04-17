package org.ryuu.rbean;

import org.ryuu.rbean.annotation.Bean;
import org.ryuu.rbean.util.PathUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static org.ryuu.rbean.util.BeanUtils.*;

public class AnnotationBeanFactory implements BeanFactory {
    private final ConcurrentHashMap<String, BeanDefinition> nameBeanDefinitionMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Object> singletonBeanMap = new ConcurrentHashMap<>();

    public AnnotationBeanFactory(String packageName) {
        Path packagePath = getPackagePath(packageName);
        List<Path> beanClassPaths = getBeanClassPaths(packagePath);
        createBeanDefinitions(packageName, beanClassPaths);
        createAllEagerSingletonBeans();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getBean(String beanName, Class<T> type) {
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
        if (bean.getClass().equals(type)) {
            return (T) bean;
        }

        throw new IllegalStateException("Unexpected value: " + type);
    }

    @Override
    public <T> T getBean(Class<T> type) {
        return getBean(getBeanName(type), type);
    }

    private static Path getPackagePath(String packageName) {
        String packagePathString = packageName.replace('.', '/');
        URL url = Thread.currentThread().getContextClassLoader().getResource(packagePathString);
        return new File(Objects.requireNonNull(url).getFile()).toPath();
    }

    private List<Path> getBeanClassPaths(Path packagePath) {
        List<Path> childrenPath = new LinkedList<>();
        try {
            PathUtils.getChildren(packagePath, childrenPath);
            return childrenPath;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void createBeanDefinitions(String packageName, List<Path> childrenPath) {
        String firstPackageName = packageName.split("\\.")[0];
        for (Path path : childrenPath) {
            Class<?> type = pathToClass(path, firstPackageName);
            if (!type.isAnnotationPresent(Bean.class)) {
                continue;
            }

            String name = getBeanName(type);
            BeanDefinition definition = createBeanDefinition(type);
            nameBeanDefinitionMap.put(name, definition);
        }
    }

    private void createAllEagerSingletonBeans() {
        for (Map.Entry<String, BeanDefinition> entry : nameBeanDefinitionMap.entrySet()) {
            String name = entry.getKey();
            BeanDefinition definition = entry.getValue();
            if (
                    definition.getScopeType() == ScopeType.SINGLETON &&
                            definition.getLoadingStrategy() == LoadingStrategy.EAGER
            ) {
                Object bean = createBean(definition);
                singletonBeanMap.put(name, bean);
            }
        }
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
}
