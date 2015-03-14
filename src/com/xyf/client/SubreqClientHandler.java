package com.xyf.client;

import com.xyf.server.SubscribeReq;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by xuyifei01 on 2015/3/14.
 */
public class SubreqClientHandler extends ChannelHandlerAdapter {
    public SubreqClientHandler(){

    }
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        for(int i = 0;i< 10;i++) {
            ctx.write(subReq(i));
        }
        ctx.flush();
    }

    private SubscribeReq subReq(int i) {
        SubscribeReq req = new SubscribeReq();
        req.setAddress("XXX");
        req.setSubReqID(i);
        req.setProductName("XXXX");
        req.setPhoneNumber("sss");
        req.setUserName("xuyifei");
        return req;
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx,Object msg) {
        System.out.println("Receive server response : [" +msg + "]");
    }
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }
    public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
