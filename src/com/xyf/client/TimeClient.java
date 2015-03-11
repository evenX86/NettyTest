package com.xyf.client;

/**
 * Created by xuyifei01 on 2015/3/5.
 */
public class TimeClient {
    public static void main(String[] args) {
        int port = 8080;
        if (args != null && args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        new Thread(new TimeClientHandle("127.0.0.1",port), "NIO-Client-001").start();
    }
}
