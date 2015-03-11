package com.xyf.test;

/**
 * Created by xuyifei01 on 2015/3/9.
 */
public class TestJoin implements Runnable{

    @Override
    public void run() {
        System.out.println(0);
    }
    public static void main(String[] args) {
        TestJoin testJoin = new TestJoin();
        JoinThread joinThread = new JoinThread();
        Thread t = new Thread(testJoin);
        Thread t2 = new Thread(joinThread);
        try {
            t.start();
            t.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(2);

    }
}
class JoinThread implements Runnable{
    @Override
    public void run() {
        System.out.println(1);
    }
}
