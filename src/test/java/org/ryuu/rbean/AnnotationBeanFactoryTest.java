package org.ryuu.rbean;

import org.junit.jupiter.api.Test;
import org.ryuu.rbean.annotation.Bean;

import static org.junit.jupiter.api.Assertions.*;

class AnnotationBeanFactoryTest {

    @Test
    void getDefaultBean() {
        AnnotationBeanFactory annotationBeanFactory = new AnnotationBeanFactory("org.ryuu.rbean");
        DefaultBean defaultBean = annotationBeanFactory.getBean("defaultBean", DefaultBean.class);
        assertNotEquals(null, defaultBean);
    }

    @Bean
    static class DefaultBean {
        public DefaultBean() {
        }
    }
}