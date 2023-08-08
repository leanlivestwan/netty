package org.wsy.reactor;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Reactor implements Closeable, Runnable{

    private final ServerSocketChannel serverChannel;
    private final Selector selector;
    public Reactor() throws IOException {
        serverChannel = ServerSocketChannel.open();
        selector = Selector.open();
    }

    @Override
    public void run() {
        try {
            serverChannel.bind(new InetSocketAddress(8080));
            serverChannel.configureBlocking(false);
            //注册时，将Acceptor作为附加对象存放，当选择器选择后也可以获取到
            serverChannel.register(selector, SelectionKey.OP_ACCEPT, new Acceptor(serverChannel, selector));
            while (true) {
                int count = selector.select();
                System.out.println("监听到 "+count+" 个事件");
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    this.dispatch(iterator.next());   //通过dispatch方法进行分发
                    iterator.remove();
                }
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    //通过此方法进行分发
    private void dispatch(SelectionKey key){
        Object att = key.attachment();   //获取attachment，ServerSocketChannel和对应的客户端Channel都添加了的
        if(att instanceof Runnable) {
            ((Runnable) att).run();   //由于Handler和Acceptor都实现自Runnable接口，这里就统一调用一下
        }   //这样就实现了对应的时候调用对应的Handler或是Acceptor了
    }

    //用了记得关，保持好习惯，就像看完视频要三连一样
    @Override
    public void close() throws IOException {
        serverChannel.close();
        selector.close();
    }
}