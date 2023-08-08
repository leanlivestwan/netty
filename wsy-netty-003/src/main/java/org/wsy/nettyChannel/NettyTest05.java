package org.wsy.nettyChannel;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.nio.charset.StandardCharsets;

public class NettyTest05 {
    public static void main(String[] args) {
        EventLoopGroup bossGroup = new NioEventLoopGroup(), workerGroup = new NioEventLoopGroup(1);  //线程数先限制一下
        EventLoopGroup handlerGroup = new DefaultEventLoopGroup();  //使用DefaultEventLoop来处理其他任务
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) {
                        channel.pipeline()
                                .addLast(new ChannelInboundHandlerAdapter(){
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        ByteBuf buf = (ByteBuf) msg;
                                        System.out.println("接收到客户端发送的数据："+buf.toString(StandardCharsets.UTF_8));
                                        handlerGroup.submit(() -> {
                                            //由于继承自ScheduledExecutorService，我们直接提交任务就行了，是不是感觉贼方便
                                            try {
                                                Thread.sleep(10000);
                                            } catch (InterruptedException e) {
                                                throw new RuntimeException(e);
                                            }
                                            ctx.writeAndFlush(Unpooled.wrappedBuffer("已收到！".getBytes()));
                                        });
                                    }
                                });
                    }
                });
        bootstrap.bind(8080);
    }
}
