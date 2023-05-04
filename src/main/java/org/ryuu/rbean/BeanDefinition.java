package org.ryuu.rbean;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BeanDefinition {
    private Class<?> type;

    private ScopeType scopeType = ScopeType.SINGLETON;

    private LoadingStrategy loadingStrategy = LoadingStrategy.EAGER;

    private List<String> dependencies;
}
