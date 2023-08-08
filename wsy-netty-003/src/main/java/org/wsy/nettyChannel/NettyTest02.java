package org.wsy.nettyChannel;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.nio.charset.StandardCharsets;

public class NettyTest02 {
    public static void main(String[] args) {
        EventLoopGroup bossGroup = new NioEventLoopGroup(), workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                //ChannelInitializer是一个特殊的ChannelHandler，它本身不处理任何出站/入站事件，它的目的仅仅是完成Channel的初始化
                .childHandler(new ChannelInitializer<SocketChannel>() {   //注意，这里的SocketChannel不是我们NIO里面的，是Netty的
                    @Override
                    protected void initChannel(SocketChannel channel) {
                        channel.pipeline()   //直接获取pipeline，然后添加两个Handler，注意顺序
                                .addLast(new ChannelInboundHandlerAdapter(){   //第一个用于处理消息接收
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        ByteBuf buf = (ByteBuf) msg;
                                        System.out.println("接收到客户端发送的数据："+buf.toString(StandardCharsets.UTF_8));
                                        throw new RuntimeException("我是异常");
                                    }
                                })
                                .addLast(new ChannelInboundHandlerAdapter(){   //第二个用于处理异常
                                    @Override
                                    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                        System.out.println("我是异常处理："+cause);
                                    }
                                });
                    }
                });
        bootstrap.bind(8080);
    }
}
