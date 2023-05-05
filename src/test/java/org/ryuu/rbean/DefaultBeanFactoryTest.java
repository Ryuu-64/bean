package org.ryuu.rbean;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ryuu.rbean.factory.DefaultBeanFactory;
import org.ryuu.rbean.util.BeanUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

class DefaultBeanFactoryTest {
    private DefaultBeanFactory defaultBeanFactory;

    @BeforeEach
    void setUp() {
        Map<String, BeanDefinition> nameBeanDefinitionMap = new HashMap<>();
        nameBeanDefinitionMap.put(
                BeanUtils.getDefaultBeanName(DefaultBean.class),
                BeanDefinition.builder()
                        .type(DefaultBean.class)
                        .dependencies(Collections.singletonList("defaultBean"))
                        .build()
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