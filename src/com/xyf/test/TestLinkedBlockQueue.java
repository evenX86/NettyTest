package com.xyf.test;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by xuyifei01 on 2015/3/13.
 */
public class TestLinkedBlockQueue {
    public static void main(String[] args) {
        LinkedBlockingQueue<String>queue = new LinkedBlockingQueue<>(5);
        for (int i=0;i<10;i++) {
            queue.add("eee");
        }
    }
}
