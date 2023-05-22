# bean

## 如何使用

### @Bean

1. 使用 `@Bean` 标记需要的 Bean

   ```java
   @Bean
   public static class DefaultBean {
       public DefaultBean() {
       }
   }
   ```

2. 创建 BeanFactory

   ```java
   BeanFactory factory = new AnnotationBeanFactory("your.package.name");
   ```

   如果使用 Android 并开启了混淆，需要手动指定 Bean 所在的类

   ```java
   BeanFactory factory = new AnnotationBeanFactory(
       The.class,
       Bean.class,
       Classes.class
   );
   ```

3. 获取 Bean

   ```java
   factory.getBean(DefaultBean.class);
   ```
   
   或 

   ```java
   factory.getBean("defaultBean", DefaultBean.class);
   ```

### @Bean.Scope



### @Bean.Loading



### @Bean.DependOn

当 Bean 依赖于其他 Bean 时，使用 @Bean.DependOn 注解标记他的依赖：

```java
@Bean
@Bean.DependOn(dependencies = {"defaultBean"})
public static class DependenciesBean {
    public DependenciesBean() {
    }
}
```

这将改变 Bean 的创建顺序，保证被依赖的 Bean 先创建。

## 原理

`@Bean`

为方法或类添加 `@Bean` 注解将为其生成一个 `BeanDefinition`