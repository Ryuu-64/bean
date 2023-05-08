package org.ryuu.bean.factory;

import org.ryuu.bean.BeanDefinition;

import java.util.Map;


public class DefaultBeanFactory extends AbstractBeanFactory {
    public DefaultBeanFactory(Map<String, BeanDefinition> nameBeanDefinitionMap) {
        this.nameBeanDefinitionMap.putAll(nameBeanDefinitionMap);
        createAllEagerSingletonBeans();
    }
}
