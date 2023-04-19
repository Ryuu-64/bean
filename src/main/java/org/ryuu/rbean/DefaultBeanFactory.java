package org.ryuu.rbean;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;


public class DefaultBeanFactory extends AbstractBeanFactory {
    public DefaultBeanFactory(Map<String, BeanDefinition> nameBeanDefinitionMap) {
        this.nameBeanDefinitionMap = Collections.synchronizedMap(new LinkedHashMap<>());
        this.nameBeanDefinitionMap.putAll(nameBeanDefinitionMap);
        singletonBeanMap = Collections.synchronizedMap(new LinkedHashMap<>());
        createAllEagerSingletonBeans();
    }
}
