package org.ryuu.bean;

import lombok.*;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor
@Builder
@Getter
@ToString
public class BeanDefinition {
    @NonNull
    private String name;

    private String parentName;

    private Method method;

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
