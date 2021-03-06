package com.xyf.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

import java.nio.channels.SocketChannel;

/**
 * Created by xuyifei01 on 2015/3/11.
 */
public class NettyServer {

    public void bind(int port) {
            EventLoopGroup bossGroup = new NioEventLoopGroup(); //配置服务端的NIO线程组。
            EventLoopGroup workGroup = new NioEventLoopGroup();//包含了一组NIO线程组，实际上他们就是reactor线程组。创建两个的原因一个是用于服务端接受客户端的连接，另一个是用于进行SocketChannel的网络读写。
        try {
            ServerBootstrap b = new ServerBootstrap();//ServerBootstrapNetty启动NIO服务端的辅助启动类
            b.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)//设置创建的channel为NioServerSocketChannel,对应着NIO类库里的ServerSocketChannel
                    .option(ChannelOption.SO_BACKLOG, 1024)//设置NioServerSocketChannel的TCP参数,这里是最大连接数为1024
                    .childHandler(new ChildChannelHandler());//绑定IO事件的处理类ChildChannelHandler
            ChannelFuture f = b.bind(port).sync();//绑定端口
            f.channel().closeFuture().sync();//等待服务端监听端口关闭
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    /**
     * LineBasedFrameDecoder 运行原理是依次遍历ByteBuf中的可读字节，判断看是否有'\n'，或者'\r\n',如果有就以此作为结束位置。
     * LineBasedFrameDecoder+StringDecoder 按行切换的文本解码器。
     */
    private class ChildChannelHandler extends ChannelInitializer {
        @Override
        protected void initChannel(Channel ch) throws Exception {
          //  ch.pipeline().addLast(new NettyServerHandler());
            //处理闭包的情况
            ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
            ch.pipeline().addLast(new StringDecoder());
            ch.pipeline().addLast(new NettyServerUnSafeTCPHandler());
        }
    }
    public static void main(String[] args) {
        int port = 8080;
        if (args != null && args.length > 0) {
            try {
                port = Integer.valueOf(args[0]);
            } catch (Exception e) {

            }
        }
        new NettyServer().bind(port);
    }
}
