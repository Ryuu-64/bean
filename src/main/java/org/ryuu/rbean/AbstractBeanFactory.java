package org.ryuu.rbean;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.ryuu.rbean.util.BeanUtils.createBean;
import static org.ryuu.rbean.util.BeanUtils.getBeanName;

public abstract class AbstractBeanFactory implements BeanFactory {
    protected final ConcurrentHashMap<String, BeanDefinition> nameBeanDefinitionMap = new ConcurrentHashMap<>();

    protected final ConcurrentHashMap<String, Object> singletonBeanMap = new ConcurrentHashMap<>();

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
}