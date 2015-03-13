package com.xyf.client;

import javax.annotation.processing.SupportedSourceVersion;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by xuyifei01 on 2015/3/5.
 */
public class TimeClientHandle implements Runnable {
    private String host;
    private int port;
    private Selector selector;
    private SocketChannel socketChannel;
    private volatile boolean stop;

    public TimeClientHandle(String host, int port) {
        this.host = host == null?"127.0.0.1":host;
        this.port = port;
        try {
            selector = Selector.open();
            socketChannel = SocketChannel.open();//打开SocketChannel
            socketChannel.configureBlocking(false);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

    }

    @Override
    public void run() {
        try {
            doConnect();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        while(!stop ){
            try {
                selector.select(1000);
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                SelectionKey key = null;
                Iterator<SelectionKey> it = selectedKeys.iterator();
                while (it.hasNext()) {
                    key = it.next();
                    it.remove();
                    try {
                        handleInput(key);
                    } catch (Exception e) {
                        if (key!=null) {
                            key.cancel();
                            if (key.channel()!=null) {
                                key.channel().close();
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
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

    private void doWrite(SocketChannel sc) throws IOException {
        byte[] req = "QUERY TIME ORDER".getBytes();
        ByteBuffer  writeBuffer = ByteBuffer.allocate(req.length);
        writeBuffer.put(req);
        writeBuffer.flip();
        sc.write(writeBuffer);
        if (!writeBuffer.hasRemaining()) {
            System.out.println("Send Order 2 Server Succeed");
        }

    }

    private void handleInput(SelectionKey key)  throws IOException{
        if (key.isValid()) {
            SocketChannel sc = (SocketChannel) key.channel();
            if (key.isConnectable()) {
                if (sc.finishConnect()) {
                    sc.register(selector,SelectionKey.OP_READ);
                    doWrite(sc);
                } else {
                    System.out.println(1);
                    System.exit(1);
                }

            } else {
                System.exit(1);
            }
            if (key.isReadable()) {
                ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                int readBytes = sc.read(readBuffer);
                if (readBytes >0 ) {
                    readBuffer.flip();
                    byte[] bytes = new byte[readBuffer.remaining()];
                    readBuffer.get(bytes);
                    String body = new String(bytes,"utf-8");
                    System.out.println("Now is : " + body);
                    this.stop = true;
                } else if (readBytes < 0) {
                    key.cancel();
                    sc.close();
                } else {

                }
            }
        }



    }

    private void doConnect() throws IOException {
        if (socketChannel.connect(new InetSocketAddress(host,port))) {
            socketChannel.register(selector,SelectionKey.OP_READ);
            doWrite(socketChannel);//连接成功直接注册读状态到多路复用器中
        } else {
            socketChannel.register(selector,SelectionKey.OP_CONNECT);//没有连接成功说明物理链路还没有建立，监听TCP的ACK应答
        }

    }

}
