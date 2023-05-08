package org.ryuu.bean.factory;

public interface BeanFactory {
    int getBeanDefinitionCount();

    String[] getBeanDefinitionNames();

    <T> T getBean(String name, Class<T> type);

    <T> T getBean(Class<T> type);
}
