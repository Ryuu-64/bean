package org.ryuu.rbean;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ryuu.rbean.annotation.Bean;
import org.ryuu.rbean.annotation.Scope;

import static org.junit.jupiter.api.Assertions.*;

class AnnotationBeanFactoryTest {
    private AnnotationBeanFactory annotationBeanFactory;

    @BeforeEach
    void setUp() {
        annotationBeanFactory = new AnnotationBeanFactory("org.ryuu");
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

    @Bean
    static class DefaultBean {
        public DefaultBean() {
        }
    }

    @Bean
    @Scope(scopeType = ScopeType.SINGLETON)
    static class SingletonBean {
        public SingletonBean() {
        }
    }

    @Bean
    @Scope(scopeType = ScopeType.PROTOTYPE)
    static class PrototypeBean {
        public PrototypeBean() {
        }
    }
}