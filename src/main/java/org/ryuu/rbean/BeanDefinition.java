package org.ryuu.rbean;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BeanDefinition {
    private Class<?> type;

    private ScopeType scopeType = ScopeType.SINGLETON;

    private LoadingStrategy loadingStrategy = LoadingStrategy.EAGER;

    public BeanDefinition(Class<?> type) {
        this.type = type;
    }
}
