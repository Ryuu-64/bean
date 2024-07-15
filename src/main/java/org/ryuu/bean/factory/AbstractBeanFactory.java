package org.ryuu.bean.factory;

import org.ryuu.bean.BeanDefinition;
import org.ryuu.bean.LoadingStrategy;
import org.ryuu.bean.ScopeType;
import org.ryuu.bean.math.util.DirectedAcyclicGraphUtils;
import org.ryuu.bean.util.Beans;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public abstract class AbstractBeanFactory implements BeanFactory {
    protected final Map<String, BeanDefinition> nameBeanDefinitionMap = new ConcurrentHashMap<>();

    protected final Map<String, Object> singletonBeanMap = new ConcurrentHashMap<>();

    @Override
    public int getBeanDefinitionCount() {
        return nameBeanDefinitionMap.size();
    }

    @Override
    public String[] getBeanDefinitionNames() {
        Set<String> keySet = nameBeanDefinitionMap.keySet();
        return keySet.toArray(new String[0]);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getBean(String beanName, Class<T> type) {
        if (beanName == null) {
            return null;
        }

        BeanDefinition beanDefinition = nameBeanDefinitionMap.get(beanName);
        if (beanDefinition == null) {
            return null;
        }

        Object bean;
        switch (beanDefinition.getScopeType()) {
            case SINGLETON:
                bean = singletonBeanMap.get(beanName);
                if (bean == null) {
                    bean = Beans.create(beanDefinition, this);
                    singletonBeanMap.put(beanDefinition.getName(), bean);
                }
                return (T) bean;
            case PROTOTYPE:
                bean = Beans.create(beanDefinition, this);
                return (T) bean;
            default:
                throw new IllegalStateException("Unexpected value: " + beanDefinition.getScopeType());
        }

    }

    @Override
    public <T> T getBean(Class<T> type) {
        Optional<String> optionalName = Beans.TypeBeans.getBeanName(type);
        return optionalName.map(name -> getBean(name, type)).orElse(null);
    }

    protected void createAllEagerSingletonBeans() {
        Map<String, List<String>> names = new HashMap<>();
        for (Map.Entry<String, BeanDefinition> entry : nameBeanDefinitionMap.entrySet()) {
            String key = entry.getKey();
            BeanDefinition beanDefinition = entry.getValue();
            if (beanDefinition.getScopeType() == ScopeType.PROTOTYPE) {
                continue;
            }

            if (beanDefinition.getLoadingStrategy() == LoadingStrategy.LAZY) {
                continue;
            }

            names.put(key, beanDefinition.getDependencies());
        }

        List<String> sortedNames = DirectedAcyclicGraphUtils.kahnTopologicalSort(names);

        Collections.reverse(sortedNames);

        for (String name : sortedNames) {
            BeanDefinition definition = nameBeanDefinitionMap.get(name);
            if (
                    definition.getScopeType() == ScopeType.SINGLETON &&
                            definition.getLoadingStrategy() == LoadingStrategy.EAGER
            ) {
                Object bean = Beans.create(definition, this);
                singletonBeanMap.put(name, bean);
            }
        }
    }
}