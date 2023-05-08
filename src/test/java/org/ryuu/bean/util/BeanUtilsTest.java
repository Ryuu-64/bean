package org.ryuu.bean.util;

import org.junit.jupiter.api.Test;
import org.ryuu.bean.BeanDefinition;
import org.ryuu.bean.LoadingStrategy;
import org.ryuu.bean.ScopeType;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class BeanUtilsTest {
    private static class TestBean {
        public TestBean() {
        }
    }

    @Test
    void createBeanDefinition() {
        BeanDefinition beanDefinition = BeanUtils.createBeanDefinition(TestBean.class);
        assertEquals(TestBean.class, beanDefinition.getType());
        assertEquals(ScopeType.SINGLETON, beanDefinition.getScopeType());
        assertEquals(LoadingStrategy.EAGER, beanDefinition.getLoadingStrategy());
        assertEquals(Collections.emptyList(), beanDefinition.getDependencies());
    }

    void createBean() {
    }

    @Test
    void getBeanName() {
    }

    @Test
    void getDefaultBeanName() {
    }
}