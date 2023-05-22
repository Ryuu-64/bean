package org.ryuu.bean;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class BeanDefinitionTest {
    @Test
    void build() {
        BeanDefinition beanDefinition = BeanDefinition
                .builder()
                .name("build")
                .type(Object.class)
                .build();
        System.out.println(beanDefinition.toString());
    }

    @Test
    void nullType() {
        try {
            BeanDefinition.builder().build();
        } catch (Exception e) {
            assertTrue(e instanceof NullPointerException);
        }
    }

    @Test
    void nullDependencies() {
        try {
            BeanDefinition
                    .builder()
                    .dependencies(null)
                    .build();
        } catch (Exception e) {
            assertTrue(e instanceof NullPointerException);
        }
    }
}