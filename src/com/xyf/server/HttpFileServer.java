package com.xyf.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * HTTP文件服务器
 * Created by xuyifei01 on 2015/3/15.
 */
public class HttpFileServer {
    private static final String DEFAULT_URL = "/src/com/xyf/netty/";
    public void run(final int port,final String url) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ch.pipeline().addLast("http-decoder",new HttpRequestDecoder());//增加HTTP请求信息解码器
                            ch.pipeline().addLast("http-aggregator",new HttpObjectAggregator(65536));//该解码器作用是将多个信息转换为单一的FullHttpRequest或者Response
                            ch.pipeline().addLast("http-encoder",new HttpRequestEncoder());
                            ch.pipeline().addLast("http-chunked",new ChunkedWriteHandler());//支持异步发送大的码流，但不占用过多的内存
                            ch.pipeline().addLast("fileServerHandler",new HttpFileServerHandler(url));//文件服务器的业务逻辑处理
                        }
                    });
            ChannelFuture f = b.bind("192.168.1.102",port).sync();
            System.out.println("HTTP 文件目录服务器启动，网址是 : "+ "http//192.168.1.102:"+port+url);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {

        }
    }
    public static void main(String[] args) {
        new HttpFileServer().run(8080,DEFAULT_URL);
    }
}
