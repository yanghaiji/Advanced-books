package com.javayh.advanced.java.java.patterns.factory;

import com.javayh.advanced.java.java.patterns.factory.bean.Tomcat;

/**
 * <p>
 *
 * </p>
 *
 * @author hai ji
 * @version 1.0.0
 * @since 2020-09-14
 */
public class FactoryTest {
    public static void main(String[] args) {
        ServerFactory<Tomcat> serverFactory = new TomcatWebServerFactory();
        serverFactory.create(8888);
        serverFactory.start();
        serverFactory.after();
    }
}
