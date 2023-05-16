package org.ryuu.bean;

import lombok.*;

import java.util.Collections;
import java.util.List;

@AllArgsConstructor
@Builder
@Getter
public class BeanDefinition {
    @NonNull
    private Class<?> type;

    @Builder.Default
    @NonNull
    private ScopeType scopeType = ScopeType.SINGLETON;

    @Builder.Default
    @NonNull
    private LoadingStrategy loadingStrategy = LoadingStrategy.EAGER;

    @Builder.Default
    @NonNull
    private List<String> dependencies = Collections.emptyList();
}
