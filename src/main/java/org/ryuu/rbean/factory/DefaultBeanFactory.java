package org.ryuu.rbean.factory;

import org.ryuu.rbean.BeanDefinition;

import java.util.Map;


public class DefaultBeanFactory extends AbstractBeanFactory {
    public DefaultBeanFactory(Map<String, BeanDefinition> nameBeanDefinitionMap) {
        this.nameBeanDefinitionMap.putAll(nameBeanDefinitionMap);
        createAllEagerSingletonBeans();
    }
}
