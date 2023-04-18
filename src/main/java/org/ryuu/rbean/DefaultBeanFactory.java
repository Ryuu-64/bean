package org.ryuu.rbean;

import java.util.concurrent.ConcurrentHashMap;


public class DefaultBeanFactory extends AbstractBeanFactory {
    public DefaultBeanFactory(ConcurrentHashMap<String, BeanDefinition> nameBeanDefinitionMap) {
        this.nameBeanDefinitionMap.putAll(nameBeanDefinitionMap);
        createAllEagerSingletonBeans();
    }
}
