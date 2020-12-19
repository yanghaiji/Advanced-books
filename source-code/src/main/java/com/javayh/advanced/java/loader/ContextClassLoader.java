package com.javayh.advanced.java.loader;

import org.aspectj.util.Reflection;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

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

    public ContextClassLoader(ClassLoader parent, String path) {
        super(parent);
        this.path = path;
    }

    public ContextClassLoader(String path) {
        this.path = path;
    }

    @Override
    protected Class<?> findClass(String className) throws ClassNotFoundException {
        ByteArrayOutputStream bos = null;
        BufferedInputStream bis = null;
        try {
            //获取字节码文件的完成路径
            String fileName = path + className + ".class";
            bis = new BufferedInputStream(new FileInputStream(fileName));
            bos = new ByteArrayOutputStream();
            //读入写出数据的过程
            int len;
            byte[] data = new byte[1024];
            while ((len = bis.read(data)) != -1) {
                bos.write(data, 0, len);
            }
            //获取内存中的完整的字节数组的数据
            byte[] bytes = bos.toByteArray();
            //将字节数组的数据返回给 class 对象
            return defineClass(null, bytes, 0, bytes.length);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            try {
                if (bos != null) {

                    bos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (bis != null) {
                    bis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}

