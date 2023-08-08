package org.wsy.juc;

public class JUC_09 {
    public static void main(String[] args) {
        ThreadLocal<String> local = new InheritableThreadLocal<>();
        Thread t = new Thread(() -> {
            local.set("yyds");
            new Thread(() -> {
                System.out.println(local.get());
            }).start();
        });
        t.start();
    }
}
