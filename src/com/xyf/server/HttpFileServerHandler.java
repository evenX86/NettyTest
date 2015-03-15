package com.xyf.server;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 文件服务器的业务逻辑处理
 * Created by xuyifei01 on 2015/3/15.
 */
public class HttpFileServerHandler extends SimpleChannelInboundHandler {

    public HttpFileServerHandler(String url) {

    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Object msg) throws Exception {

    }
}
