package com.xyf.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;


/**
 * Created by xuyifei01 on 2015/3/11.
 */
public class NettyClient {
    public void connect(int port,String host) {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY,true)
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                       //     ch.pipeline().addLast(new NettyClientHandler());//
                              ch.pipeline().addLast(new NettyClientUnsafeTCPHandler());//
                        }
                    });

            ChannelFuture f = b.connect(host,port).sync();
            f.channel().closeFuture().sync();
        } catch (Exception e){

        } finally {
            group.shutdownGracefully();

        }

    }
    public static void main(String[] args) {
        int port = 8080;
        new NettyClient().connect(port,"127.0.0.1");
    }
}
