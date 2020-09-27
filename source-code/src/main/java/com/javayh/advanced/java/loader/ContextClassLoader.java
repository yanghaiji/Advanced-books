package com.javayh.advanced.java.loader;

import org.aspectj.util.Reflection;

/**
 * <p>
 *
 * </p>
 *
 * @author hai ji
 * @version 1.0.0
 * @since 2020-09-14
 */
public class ContextClassLoader extends ClassLoader {

    private String path;
    private String classLoaderName;
    private ClassLoader classLoader;

    public ContextClassLoader() {
        super();
    }

    public ContextClassLoader(String path, String classLoaderName) {
        this.path = path;
        this.classLoaderName = classLoaderName;
    }

    public ContextClassLoader(ClassLoader classLoader) {
        super(classLoader);
    }

}
