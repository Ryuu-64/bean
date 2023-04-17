package org.ryuu.rbean;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.ryuu.rbean.util.BeanUtils.createBean;


public class DefaultBeanFactory implements BeanFactory {
    private final ConcurrentHashMap<String, BeanDefinition> nameBeanDefinitionMap;

    private final ConcurrentHashMap<String, Object> singletonBeanMap = new ConcurrentHashMap<>();

    public DefaultBeanFactory(ConcurrentHashMap<String, BeanDefinition> nameBeanDefinitionMap) {
        this.nameBeanDefinitionMap = nameBeanDefinitionMap;
        createAllEagerSingletonBeans();
    }

    @Override
    public <T> T getBean(String name, Class<T> type) {
        return null;
    }

    @Override
    public <T> T getBean(Class<T> type) {
        return null;
    }

    private void createAllEagerSingletonBeans() {
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
