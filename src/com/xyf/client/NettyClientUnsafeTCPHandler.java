package com.xyf.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.io.UnsupportedEncodingException;

/**
 * Created by xuyifei01 on 2015/3/13.
 */
public class NettyClientUnsafeTCPHandler extends ChannelHandlerAdapter{
    private int counter;
    private byte[] req;
    public NettyClientUnsafeTCPHandler(){
        req = ("QUERY TIME ORDER" + System.getProperty("line.separator")).getBytes();

    }
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ByteBuf message = null;
        for (int i=0;i<100;i++) {
            message = Unpooled.buffer(req.length);
            message.writeBytes(req);
            ctx.writeAndFlush(message);
        }

    }
    public void channelRead(ChannelHandlerContext ctx,Object msg) {
        String body = (String) msg;
        System.out.println("Now is : " + body + "; The Counter is :" + ++counter);
    }
    public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause) {
        ctx.close();
    }
}
