package org.ryuu.rbean;

import lombok.Getter;
import lombok.Setter;

public class BeanDefinition {
    @Setter
    @Getter
    private Class<?> type;

    @Setter
    @Getter
    private Scope scope;
}
