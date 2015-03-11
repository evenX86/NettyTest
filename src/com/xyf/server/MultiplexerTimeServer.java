package com.xyf.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by xuyifei01 on 2015/3/5.
 */
public class MultiplexerTimeServer implements Runnable {
    private Selector selector;
    private ServerSocketChannel serverChannel;
    private volatile boolean stop;

    /**
     * 初始化多路复用器，绑定监听端口
     *
     * @param port
     */
    public MultiplexerTimeServer(int port) {
        try {
            selector = Selector.open(); //创建多路复用器selector
            serverChannel = ServerSocketChannel.open();//创建一个用于建立连接的ServerSocketChannel
            serverChannel.configureBlocking(false); //设置异步模式
            serverChannel.socket().bind(new InetSocketAddress(port), 1024);
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);//将ServerSocketChannel注册到Selector ， 监听SelectionKey.OP_ACCEPT操作位
            System.out.println("Server is start in : " + port);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void stop() {
        this.stop = true;
    }

    @Override
    public void run() {
        while (!stop) {
            try {
                selector.select(1000);  //休眠一秒，循环遍历selector
                Set<SelectionKey> selectionKeySet = selector.selectedKeys();
                Iterator<SelectionKey> it = selectionKeySet.iterator();
                SelectionKey key = null;
                while (it.hasNext()) {
                    key = it.next();
                    it.remove();
                    try {
                        handleInput(key);
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (key != null) {
                            key.cancel();
                            if (key.channel() != null) {
                                key.channel().close();
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (selector != null) {
            try {
                selector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *
     * @param key
     * @throws IOException
     */
    private void handleInput(SelectionKey key) throws IOException {
        if (key.isValid()) {
            if (key.isAcceptable()) {   //处理新接入的客户端请求信息
                ServerSocketChannel ssc = (ServerSocketChannel) key.channel();  //获取注册的ServerSocketChannel
                SocketChannel sc = ssc.accept();    //完成了TCP的三次握手，TCP的物理链路正式建立
                sc.configureBlocking(false);
                sc.register(selector, SelectionKey.OP_READ);
            }
            if (key.isReadable()) {  //读取客户端的请求信息
                SocketChannel sc = (SocketChannel) key.channel();
                ByteBuffer readBuffer = ByteBuffer.allocate(1024);//开辟一个1M的缓冲区
                int readBytes = sc.read(readBuffer);//请求码流
                if (readBytes > 0) { //读到了字节，对字节进行编解码
                    readBuffer.flip();//将缓冲区当前的limit设置为position，position设置为0
                    byte[] bytes = new byte[readBuffer.remaining()];
                    readBuffer.get(bytes);
                    String body = new String(bytes,"utf-8");
                    System.out.println("Server Receive order :" + body);
                    String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body)?new Date(System.currentTimeMillis()).toString():"BAD ORDER";
                    doWrite(sc,currentTime);
                } else if (readBytes < 0) {
                    //链路已经关闭，需要关闭SocketChannel
                    key.cancel();
                    sc.close();
                } else {
                    //一般场景，忽略

                }
            }
        }
    }

    /***
     *  将应答信息异步发送给客户端
     *  可能会出现写半包的情况
     * @param sc
     * @param response
     * @throws IOException
     */
    private void doWrite(SocketChannel sc, String response) throws IOException {
        if (response != null && response.trim().length()>0) {
            byte[] bytes = response.getBytes();
            ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
            writeBuffer.put(bytes);
            writeBuffer.flip();
            sc.write(writeBuffer);
        }
    }
}





















