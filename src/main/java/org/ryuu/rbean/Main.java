package org.ryuu.rbean;

import org.ryuu.rbean.test.A.TestService;

@PackageScan(packageName = "org.ryuu.rbean")
public class Main {
    public static void main(String[] args) {
        JBeanFactory jBeanFactory = new JBeanFactory(Main.class);
        TestService testService = jBeanFactory.getBean("testService", TestService.class);
        System.out.println(testService);
    }
}
