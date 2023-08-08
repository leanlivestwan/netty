package org.wsy.last;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//SubReactor作为从Reactor
public class SubReactor implements Runnable, Closeable {
        //每个从Reactor也有一个Selector
    private final Selector selector;
    
      //创建一个4线程的线程池，也就是四个从Reactor工作
    private static final ExecutorService POOL = Executors.newFixedThreadPool(4);
    private static final SubReactor[] reactors = new SubReactor[4];
    private static int selectedIndex = 0;  //采用轮询机制，每接受一个新的连接，就轮询分配给四个从Reactor
    static {   //在一开始的时候就让4个从Reactor跑起来
        for (int i = 0; i < 4; i++) {
            try {
                reactors[i] = new SubReactor();
                POOL.submit(reactors[i]);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
        //轮询获取下一个Selector（Acceptor用）
    public static Selector nextSelector(){
        Selector selector = reactors[selectedIndex].selector;
        selectedIndex = (selectedIndex + 1) % 4;
        return selector;
    }

    private SubReactor() throws IOException {
        selector = Selector.open();
    }

    @Override
    public void run() {
        try {   //启动后直接等待selector监听到对应的事件即可，其他的操作逻辑和Reactor一致
            while (true) {
                int count = selector.select();
                System.out.println(Thread.currentThread().getName()+" >> 监听到 "+count+" 个事件");
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    this.dispatch(iterator.next());
                    iterator.remove();
                }
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void dispatch(SelectionKey key){
        Object att = key.attachment();
        if(att instanceof Runnable) {
            ((Runnable) att).run();
        }
    }

    @Override
    public void close() throws IOException {
        selector.close();
    }
}