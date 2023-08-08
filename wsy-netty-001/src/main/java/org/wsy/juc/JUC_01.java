package org.wsy.juc;

public class JUC_01 {
    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 50; i++) {
                System.out.println("我是一号线程："+i);
            }
        });
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 50; i++) {
                System.out.println("我是二号线程："+i);
            }
        });
        t1.start();
        t2.start();
    }
}
