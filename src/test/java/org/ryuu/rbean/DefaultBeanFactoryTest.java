package org.ryuu.rbean;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ryuu.rbean.factory.DefaultBeanFactory;
import org.ryuu.rbean.util.BeanUtils;

import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

class DefaultBeanFactoryTest {
    private DefaultBeanFactory defaultBeanFactory;

    @BeforeEach
    void setUp() {
        ConcurrentHashMap<String, BeanDefinition> nameBeanDefinitionMap = new ConcurrentHashMap<>();
        nameBeanDefinitionMap.put(
                BeanUtils.getDefaultBeanName(DefaultBean.class),
                new BeanDefinition(
                        DefaultBean.class, ScopeType.SINGLETON,
                        LoadingStrategy.EAGER, Collections.singletonList("defaultBean")
                )
        );
        defaultBeanFactory = new DefaultBeanFactory(nameBeanDefinitionMap);
    }

    @Test
    void getBeanByType() {
        DefaultBean defaultBean = defaultBeanFactory.getBean(DefaultBean.class);
        assertNotEquals(null, defaultBean);
    }

    @Test
    void getBeanByNameAndType() {
        DefaultBean defaultBean = defaultBeanFactory.getBean("defaultBean", DefaultBean.class);
        assertNotEquals(null, defaultBean);
    }

    public static class DefaultBean {
        public DefaultBean() {
        }
    }
}