package org.ryuu.bean;

import lombok.Getter;
import org.ryuu.bean.factory.BeanFactory;

public class BeanContext {
    @Getter
    private final BeanFactory beanFactory;

    public BeanContext(BeanFactory beanFactory) {
        if (beanFactory == null) {
            throw new IllegalArgumentException("Bean factory can't be null.");
        }

        this.beanFactory = beanFactory;
    }
}
