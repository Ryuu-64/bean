package org.ryuu.bean;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BeanContextHolder {
    @Getter
    private static volatile BeanContext beanContext;

    public static synchronized void setBeanContext(BeanContext beanContext) {
        BeanContextHolder.beanContext = beanContext;
    }
}
