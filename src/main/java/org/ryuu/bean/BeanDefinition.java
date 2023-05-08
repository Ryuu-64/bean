package org.ryuu.bean;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeanDefinition {
    private Class<?> type;

    @Builder.Default
    private ScopeType scopeType = ScopeType.SINGLETON;

    @Builder.Default
    private LoadingStrategy loadingStrategy = LoadingStrategy.EAGER;

    private List<String> dependencies;
}
