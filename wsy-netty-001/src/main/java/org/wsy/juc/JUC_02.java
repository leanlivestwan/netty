package org.wsy.juc;

public class JUC_02 {
    public static void main(String[] args) {
        Thread t = new Thread(() -> {
            try {
                Thread.sleep(10000);  //休眠10秒
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        t.start();
        try {
            Thread.sleep(3000);   //休眠3秒，一定比线程t先醒来
            t.interrupt();   //调用t的interrupt方法
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
