package org.wsy.encoder;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline()
                                .addLast(new StringDecoder())  //解码器安排
                                .addLast(new ChannelInboundHandlerAdapter(){
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        System.out.println(">> 接收到客户端发送的数据：" + msg);  //直接接收字符串
                                    }
                                })
                                .addLast(new StringEncoder());  //编码器安排
                    }
                });
        Channel channel = bootstrap.connect("localhost", 8080).channel();
        try(Scanner scanner = new Scanner(System.in)){
            while (true) {
                System.out.println("<< 请输入要发送给服务端的内容：");
                String text = scanner.nextLine();
                if(text.isEmpty()) continue;
                channel.writeAndFlush(text);  //直接发送字符串就行
            }
        }
    }
}
