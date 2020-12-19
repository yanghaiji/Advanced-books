package com.javayh.advanced.java.loader;

/**
 * <p>
 *
 * </p>
 *
 * @author hai ji
 * @version 1.0.0
 * @since 2020-10-15
 */
public class ClassLoaderTest {
    public static void main(String[] args) throws ClassNotFoundException {
        // 获取系统类加载器
        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
        System.out.println(systemClassLoader);

        // 获取其上层的：扩展类加载器
        ClassLoader extClassLoader = systemClassLoader.getParent();
        System.out.println(extClassLoader);

        // 试图获取 根加载器
        ClassLoader bootstrapClassLoader = extClassLoader.getParent();
        System.out.println(bootstrapClassLoader);

        // 获取自定义加载器
        ClassLoader classLoader = ClassLoaderTest.class.getClassLoader();
        System.out.println(classLoader);

        // 获取String类型的加载器
        ClassLoader classLoader1 = String.class.getClassLoader();
        System.out.println(classLoader1);

        ContextClassLoader contextClassLoader = new ContextClassLoader("C:\\Dylan\\");
        Class advancedApplication = contextClassLoader.findClass("AdvancedApplication");
        System.out.println(advancedApplication.getClassLoader().getClass().getName());
    }
}
