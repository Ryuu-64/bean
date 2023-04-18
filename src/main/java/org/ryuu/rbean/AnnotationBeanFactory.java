package org.ryuu.rbean;

import org.ryuu.rbean.annotation.Bean;
import org.ryuu.rbean.util.PathUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static org.ryuu.rbean.util.BeanUtils.createBeanDefinition;
import static org.ryuu.rbean.util.BeanUtils.getBeanName;

public class AnnotationBeanFactory extends AbstractBeanFactory {
    public AnnotationBeanFactory(String packageName) {
        Path packagePath = getPackagePath(packageName);
        List<Path> beanClassPaths = getBeanClassPaths(packagePath);
        createBeanDefinitions(packageName, beanClassPaths);
        createAllEagerSingletonBeans();
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
