package com.javayh.advanced.java.java.patterns.factory.bean;

/**
 * <p>
 *
 * </p>
 *
 * @author hai ji
 * @version 1.0.0
 * @since 2020-09-14
 */
public class Tomcat {

    private int port;

    public Tomcat() {
        this(8080);
    }

    public Tomcat(int port) {
        assertParam(port);
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        assertParam(port);
        this.port = port;
    }

    private void assertParam(int port) {
        if (port <= 0) {
            throw new IllegalArgumentException();
        }
    }
}
