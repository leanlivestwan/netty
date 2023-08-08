package org.wsy.nettyLast;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class NettyClient {
    public static void main(String[] args) {
        Bootstrap bootstrap = new Bootstrap();   //客户端也是使用Bootstrap来启动
        bootstrap
                .group(new NioEventLoopGroup())   //客户端就没那么麻烦了，直接一个EventLoop就行，用于处理发回来的数据
                .channel(NioSocketChannel.class)   //客户端肯定就是使用SocketChannel了
                .handler(new ChannelInitializer<SocketChannel>() {   //这里的数据处理方式和服务端是一样的
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf buf = (ByteBuf) msg;
                                System.out.println(">> 接收到客户端发送的数据："+buf.toString(StandardCharsets.UTF_8));
                            }
                        });
                    }
                });
        Channel channel = bootstrap.connect("localhost", 8080).channel();  //连接后拿到对应的Channel对象
        //注意上面连接操作是异步的，调用之后会继续往下走，下面我们就正式编写客户端的数据发送代码了
        try(Scanner scanner = new Scanner(System.in)){    //还是和之前一样，扫了就发
            while (true) {
                System.out.println("<< 请输入要发送给服务端的内容：");
                String text = scanner.nextLine();
                if(text.isEmpty()) continue;
                channel.writeAndFlush(Unpooled.wrappedBuffer(text.getBytes()));  //通过Channel对象发送数据
            }
        }
    }
}
