package org.wsy.reactor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Handler implements Runnable{

    private final SocketChannel channel;

    public Handler(SocketChannel channel) {
        this.channel = channel;
    }

    @Override
    public void run() {
        try {
            ByteBuffer buffer = ByteBuffer.allocate(128);
            channel.read(buffer);
            buffer.flip();
            System.out.println("接收到客户端数据："+new String(buffer.array(), 0, buffer.remaining()));
            channel.write(ByteBuffer.wrap("已收到！".getBytes()));
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}