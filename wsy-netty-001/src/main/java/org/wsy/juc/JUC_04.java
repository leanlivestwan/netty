package org.wsy.juc;

public class JUC_04 {
    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            System.out.println("线程1开始运行！");
            for (int i = 0; i < 50; i++) {
                if(i % 5 == 0) {
                    System.out.println("让位！");
                    Thread.yield();
                }
                System.out.println("1打印："+i);
            }
            System.out.println("线程1结束！");
        });
        Thread t2 = new Thread(() -> {
            System.out.println("线程2开始运行！");
            for (int i = 0; i < 50; i++) {
                System.out.println("2打印："+i);
            }
        });
        t1.start();
        t2.start();
    }
}
