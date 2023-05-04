package org.ryuu.rbean;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ryuu.rbean.annotation.Bean;
import org.ryuu.rbean.annotation.DependOn;
import org.ryuu.rbean.annotation.Scope;
import org.ryuu.rbean.factory.AnnotationBeanFactory;
import org.ryuu.rbean.util.BeanUtils;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class AnnotationBeanFactoryTest {
    private AnnotationBeanFactory annotationBeanFactory;

    private Class<?>[] beans;

    @BeforeEach
    void setUp() {
        annotationBeanFactory = new AnnotationBeanFactory("org.ryuu");
        beans = new Class[]{
                DefaultBean.class,
                SingletonBean.class,
                PrototypeBean.class
        };
    }

    @Test
    void getBeanDefinitionCount() {
        int count = new AnnotationBeanFactory(beans).getBeanDefinitionCount();
        assertEquals(3, count);
    }

    @Test
    void getBeanDefinitionNames() {
        String[] beanDefinitionNames = Arrays
                .stream(beans)
                .map(BeanUtils::getBeanName)
                .toArray(String[]::new);
        String[] factoryBeanDefinitionNames = new AnnotationBeanFactory(beans).
                getBeanDefinitionNames();
        assertArrayEquals(beanDefinitionNames, factoryBeanDefinitionNames);
    }

    @Test
    void getBeanByType() {
        DefaultBean defaultBean = annotationBeanFactory.getBean(DefaultBean.class);
        assertNotEquals(null, defaultBean);
    }

    @Test
    void getBeanByNameAndType() {
        DefaultBean defaultBean = annotationBeanFactory.getBean("defaultBean", DefaultBean.class);
        assertNotEquals(null, defaultBean);
    }

    @Test
    void getDefaultBean() {
        DefaultBean defaultBean = annotationBeanFactory.getBean("defaultBean", DefaultBean.class);
        assertNotEquals(null, defaultBean);
    }

    @Test
    void getSingletonBean() {
        SingletonBean bean1 = annotationBeanFactory.getBean(SingletonBean.class);
        SingletonBean bean2 = annotationBeanFactory.getBean(SingletonBean.class);
        assertNotEquals(null, bean1);
        assertNotEquals(null, bean2);
        assertEquals(bean1, bean2);
    }

    @Test
    void getPrototypeBean() {
        PrototypeBean bean1 = annotationBeanFactory.getBean(PrototypeBean.class);
        PrototypeBean bean2 = annotationBeanFactory.getBean(PrototypeBean.class);
        assertNotEquals(null, bean1);
        assertNotEquals(null, bean2);
        assertNotEquals(bean1, bean2);
    }

    @Test
    void getDependenciesBean() {
        DependenciesBean bean = annotationBeanFactory.getBean(DependenciesBean.class);
        System.out.println(bean);
    }

    @Bean
    public static class DefaultBean {
        public DefaultBean() {
        }
    }

    @Bean
    @Scope(scopeType = ScopeType.SINGLETON)
    public static class SingletonBean {
        public SingletonBean() {
        }
    }

    @Bean
    @Scope(scopeType = ScopeType.PROTOTYPE)
    public static class PrototypeBean {
        public PrototypeBean() {
        }
    }

    @Bean
    @DependOn(dependencies = {"defaultBean"})
    public static class DependenciesBean {
        public DependenciesBean() {
        }
    }
}