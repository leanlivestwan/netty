package org.wsy.juc;

public class JUC_08 {
    public static void main(String[] args) throws InterruptedException {
        ThreadLocal<String> local = new ThreadLocal<>();  //注意这是一个泛型类，存储类型为我们要存放的变量类型
        Thread t1 = new Thread(() -> {
            local.set("lbwnb");   //将变量的值给予ThreadLocal
            System.out.println("变量值已设定！");
            System.out.println(local.get());   //尝试获取ThreadLocal中存放的变量
        });
        Thread t2 = new Thread(() -> {
            System.out.println(local.get());   //尝试获取ThreadLocal中存放的变量
        });
        t1.start();
        Thread.sleep(3000);    //间隔三秒
        t2.start();
    }
}
