package org.ryuu.bean.factory;

import org.ryuu.bean.BeanDefinition;
import org.ryuu.bean.LoadingStrategy;
import org.ryuu.bean.ScopeType;
import org.ryuu.bean.math.util.DirectedAcyclicGraphUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.ryuu.bean.util.BeanUtils.createBean;
import static org.ryuu.bean.util.BeanUtils.getBeanName;

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
                Object bean = createBean(definition);
                singletonBeanMap.put(name, bean);
            }
        }
    }
}