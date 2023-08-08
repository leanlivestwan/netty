package org.wsy.selector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Server {
    public static void main(String[] args) {
        try (ServerSocketChannel serverChannel = ServerSocketChannel.open();
             Selector selector = Selector.open()){   //开启一个新的Selector，这玩意也是要关闭释放资源的
            serverChannel.bind(new InetSocketAddress(8080));
            //要使用选择器进行操作，必须使用非阻塞的方式，这样才不会像阻塞IO那样卡在accept()，而是直接通过，让选择器去进行下一步操作
            serverChannel.configureBlocking(false);
            //将选择器注册到ServerSocketChannel中，后面是选择需要监听的时间，只有发生对应事件时才会进行选择，多个事件用 | 连接，注意，并不是所有的Channel都支持以下全部四个事件，可能只支持部分
            //因为是ServerSocketChannel这里我们就监听accept就可以了，等待客户端连接
            //SelectionKey.OP_CONNECT --- 连接就绪事件，表示客户端与服务器的连接已经建立成功
            //SelectionKey.OP_ACCEPT --- 接收连接事件，表示服务器监听到了客户连接，服务器可以接收这个连接了
            //SelectionKey.OP_READ --- 读 就绪事件，表示通道中已经有了可读的数据，可以执行读操作了
            //SelectionKey.OP_WRITE --- 写 就绪事件，表示已经可以向通道写数据了（这玩意比较特殊，一般情况下因为都是可以写入的，所以可能会无限循环）
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
            while (true) {   //无限循环等待新的用户网络操作
                //每次选择都可能会选出多个已经就绪的网络操作，没有操作时会暂时阻塞
                int count = selector.select();
                System.out.println("监听到 "+count+" 个事件");
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    //根据不同的事件类型，执行不同的操作即可
                    if(key.isAcceptable()) {  //如果当前ServerSocketChannel已经做好准备处理Accept
                        SocketChannel channel = serverChannel.accept();
                        System.out.println("客户端已连接，IP地址为："+channel.getRemoteAddress());
                        //现在连接就建立好了，接着我们需要将连接也注册选择器，比如我们需要当这个连接有内容可读时就进行处理
                        channel.configureBlocking(false);
                        channel.register(selector, SelectionKey.OP_READ);
                        //这样就在连接建立时完成了注册
                    } else if(key.isReadable()) {    //如果当前连接有可读的数据并且可以写，那么就开始处理
                        SocketChannel channel = (SocketChannel) key.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(128);
                        channel.read(buffer);
                        buffer.flip();
                        System.out.println("接收到客户端数据："+new String(buffer.array(), 0, buffer.remaining()));

                        //直接向通道中写入数据就行
                        channel.write(ByteBuffer.wrap("已收到！".getBytes()));
                        //别关，说不定用户还要继续通信呢
                    }
                    //处理完成后，一定记得移出迭代器，不然下次还有
                    iterator.remove();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
