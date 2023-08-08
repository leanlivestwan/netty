package org.wsy.last;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * Acceptor主要用于处理连接操作
 */
public class Acceptor implements Runnable{

    private final ServerSocketChannel serverChannel;
    private final Selector selector;

    public Acceptor(ServerSocketChannel serverChannel, Selector selector) {
        this.serverChannel = serverChannel;
        this.selector = selector;
    }

    @Override
    public void run() {
        try{
            SocketChannel channel = serverChannel.accept();
            System.out.println("客户端已连接，IP地址为："+channel.getRemoteAddress());
            channel.configureBlocking(false);
            //这里在注册时，创建好对应的Handler，这样在Reactor中分发的时候就可以直接调用Handler了
            channel.register(selector, SelectionKey.OP_READ, new Handler(channel));
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}