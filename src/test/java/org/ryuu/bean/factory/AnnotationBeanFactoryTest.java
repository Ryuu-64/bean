package org.ryuu.bean.factory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ryuu.bean.Bean;
import org.ryuu.bean.LoadingStrategy;
import org.ryuu.bean.ScopeType;

import static org.junit.jupiter.api.Assertions.*;

class AnnotationBeanFactoryTest {
    private AnnotationBeanFactory annotationBeanFactory;

    private Class<?>[] beans;

    @BeforeEach
    void setUp() {
        annotationBeanFactory = new AnnotationBeanFactory("org.ryuu");
        beans = new Class[]{
                DefaultBean.class,
                SingletonBean.class,
                PrototypeBean.class
        };
    }

    @Test
    void getBeanDefinitionCount() {
        int count = new AnnotationBeanFactory(beans).getBeanDefinitionCount();
        assertEquals(3, count);
    }

    @Test
    void getBeanByType() {
        DefaultBean defaultBean = annotationBeanFactory.getBean(DefaultBean.class);
        assertNotEquals(null, defaultBean);
    }

    @Test
    void getBeanByNameAndType() {
        DefaultBean defaultBean = annotationBeanFactory.getBean("defaultBean", DefaultBean.class);
        assertNotEquals(null, defaultBean);
    }

    @Test
    void getDefaultBean() {
        DefaultBean defaultBean = annotationBeanFactory.getBean("defaultBean", DefaultBean.class);
        assertNotEquals(null, defaultBean);
    }

    @Test
    void getSingletonBean() {
        SingletonBean bean1 = annotationBeanFactory.getBean(SingletonBean.class);
        SingletonBean bean2 = annotationBeanFactory.getBean(SingletonBean.class);
        assertNotEquals(null, bean1);
        assertNotEquals(null, bean2);
        assertEquals(bean1, bean2);
    }

    @Test
    void getPrototypeBean() {
        PrototypeBean bean1 = annotationBeanFactory.getBean(PrototypeBean.class);
        PrototypeBean bean2 = annotationBeanFactory.getBean(PrototypeBean.class);
        assertNotEquals(null, bean1);
        assertNotEquals(null, bean2);
        assertNotEquals(bean1, bean2);
    }

    @Test
    void getDependenciesBean() {
        DependenciesBean bean = annotationBeanFactory.getBean(DependenciesBean.class);
        System.out.println(bean);
    }

    @Test
    void classWithoutBeanAnnotation() {
        ClassWithoutBeanAnnotation bean = annotationBeanFactory.getBean(ClassWithoutBeanAnnotation.class);
        assertNull(bean);
    }

    @Test
    void getMethodBean() {
        String bean = annotationBeanFactory.getBean("testBean", String.class);
        assertEquals(MethodBeanHolder.testBean, bean);
    }

    @Bean
    public static class DefaultBean {
        public DefaultBean() {
        }
    }

    @Bean
    @Bean.Scope(scopeType = ScopeType.SINGLETON)
    public static class SingletonBean {
        public SingletonBean() {
        }
    }

    @Bean
    @Bean.Scope(scopeType = ScopeType.SINGLETON)
    @Bean.Loading(loadingStrategy = LoadingStrategy.EAGER)
    public static class EagerSingletonBean {
        public EagerSingletonBean() {
        }
    }

    @Bean
    @Bean.Scope(scopeType = ScopeType.SINGLETON)
    @Bean.Loading(loadingStrategy = LoadingStrategy.LAZY)
    public static class LazySingletonBean {
        public LazySingletonBean() {
        }
    }

    @Bean
    @Bean.Scope(scopeType = ScopeType.PROTOTYPE)
    public static class PrototypeBean {
        public PrototypeBean() {
        }
    }

    @Bean
    @Bean.DependOn(dependencies = {"defaultBean"})
    public static class DependenciesBean {
        public DependenciesBean() {
        }
    }

    public static class ClassWithoutBeanAnnotation {
        public ClassWithoutBeanAnnotation() {
        }
    }

    @Bean
    public static class MethodBeanHolder {
        public static final String testBean = "bean in method bean holder";

        public MethodBeanHolder() {
        }

        @SuppressWarnings("unused")
        @Bean
        public String testBean() {
            return testBean;
        }
    }
}