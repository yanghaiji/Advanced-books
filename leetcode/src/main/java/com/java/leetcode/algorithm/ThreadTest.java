package com.java.leetcode.algorithm;

/**
 * <p>
 *
 * </p>
 *
 * @author Dylan
 * @version 1.0.0
 * @since 2020-11-06 3:52 PM
 */
public class ThreadTest {
    public static void main(String[] args) {
        InfoServer infoServer = new InfoServer("测试");

        Thread thread = new Thread(infoServer);
        System.out.println(thread.isDaemon());
        thread.start();
    }
}

class InfoServer implements Runnable{

    private final String msg;

    public InfoServer(String msg) {
        this.msg = msg;
    }

    public void printInfo(String msg){
        System.out.println("线程设计:"+msg);
    }

    @Override
    public void run() {
        printInfo(msg);
    }
}
