package org.ryuu.bean;

import org.junit.jupiter.api.Test;
import org.ryuu.bean.factory.AnnotationBeanFactory;

import static org.junit.jupiter.api.Assertions.*;

public class AutowiredTest {
    @Bean
    public static class Parameter1 {
    }

    @Bean
    public static class ConstructorBean1 {
        public final Parameter1 parameter1;

        public ConstructorBean1(Parameter1 parameter1) {
            this.parameter1 = parameter1;
        }
    }

    @Bean
    public static class Parameter2 {
    }

    @Bean
    public static class ConstructorBean2 {
        public final Parameter2 parameter2;

        public ConstructorBean2(@Qualifier("parameter3") Parameter2 parameter2) {
            this.parameter2 = parameter2;
        }
    }

    @Test
    void autowiredTest() {
        AnnotationBeanFactory beanFactory = new AnnotationBeanFactory("org.ryuu.bean");

        ConstructorBean1 bean1 = beanFactory.getBean(ConstructorBean1.class);
        assertNotNull(bean1);
        assertNotNull(bean1.parameter1);

        ConstructorBean2 bean2 = beanFactory.getBean(ConstructorBean2.class);
        assertNotNull(bean2);
        assertNull(bean2.parameter2);
    }
}
