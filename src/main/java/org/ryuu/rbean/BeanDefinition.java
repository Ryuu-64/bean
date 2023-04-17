package org.ryuu.rbean;

import lombok.*;

@Setter
@Getter
@ToString
@EqualsAndHashCode
public class BeanDefinition {
    private Class<?> type;

    private ScopeType scopeType = ScopeType.SINGLETON;

    private LoadingStrategy loadingStrategy = LoadingStrategy.EAGER;
}
