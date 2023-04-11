package org.ryuu.rbean;

import org.ryuu.rbean.test.A.TestService;
import org.ryuu.rbean.util.PathUtils;

import java.beans.Introspector;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class JBeanFactory {
    private final ConcurrentHashMap<String, BeanDefinition> nameBeanDefinitionMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Object> singletonBeanMap = new ConcurrentHashMap<>();

    public JBeanFactory(Class<?> scanClass) {
        if (!scanClass.isAnnotationPresent(PackageScan.class)) {
            throw new IllegalArgumentException("scanClass");
        }
        PackageScan packageScan = scanClass.getAnnotation(PackageScan.class);
        String packageName = packageScan.packageName();
        Path packagePath = getPackagePath(packageName);
        ArrayList<Path> childrenPath = new ArrayList<>();
        try {
            getFiles(packagePath, childrenPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String basePackName = packageName.split("\\.")[0];
        for (Path path : childrenPath) {
            String pathString = PathUtils.toStringForwardSlash(path);
            String relativePathString = pathString.substring(pathString.lastIndexOf(basePackName));
            String className = relativePathString.replace('/', '.').substring(0, relativePathString.indexOf(".class"));
            try {
                Class<?> klass = Thread.currentThread().getContextClassLoader().loadClass(className);
                if (!klass.isAnnotationPresent(JBean.class)) {
                    continue;
                }
                JBean jBean = klass.getAnnotation(JBean.class);
                String beanName = jBean.name();
                if (beanName.equals("")) {
                    beanName = Introspector.decapitalize(klass.getSimpleName());
                }
                BeanDefinition beanDefinition = new BeanDefinition();
                beanDefinition.setType(klass);
                beanDefinition.setScope(jBean.scope());
                nameBeanDefinitionMap.put(beanName, beanDefinition);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        for (String name : nameBeanDefinitionMap.keySet()) {
            BeanDefinition definition = nameBeanDefinitionMap.get(name);
            if (definition.getScope() == Scope.SINGLETON) {
                Object bean = createBean(definition);
                singletonBeanMap.put(name, bean);
            }
        }
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

    public <T> T getBean(String beanName, Class<T> klass) {
        BeanDefinition beanDefinition = nameBeanDefinitionMap.get(beanName);
        if (beanDefinition == null) {
            throw new IllegalStateException("Unexpected value: " + beanName);
        }

        Object bean;
        switch (beanDefinition.getScope()) {
            case SINGLETON:
                bean = singletonBeanMap.get(beanName);
                break;
            case PROTOTYPE:
                bean = createBean(beanDefinition);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + beanDefinition.getScope());
        }
        if (bean.getClass().equals(klass)) {
            return (T) bean;
        }

        throw new IllegalStateException("Unexpected value: " + klass);
    }

    private static Path getPackagePath(String packageName) {
        String packagePathString = packageName.replace('.', '/');
        URL url = Thread.currentThread().getContextClassLoader().getResource(packagePathString);
        return new File(Objects.requireNonNull(url).getFile()).toPath();
    }

    private static void getFiles(Path rootPath, List<Path> childrenPath) throws IOException {
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(rootPath)) {
            for (Path innerPath : directoryStream) {
                if (Files.isDirectory(innerPath)) {
                    getFiles(innerPath, childrenPath);
                } else {
                    childrenPath.add(innerPath);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
