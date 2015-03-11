package com.xyf.test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestVolatile implements Runnable{

    private  int i = 0;
    public   int getVal() {
        return i;
    }
    private synchronized void increment() {
        i++;
        i++;
    }
    @Override
    public void run() {
        while (true) {
            increment();
        }
    }
    public static void main(String[] args) {
        TestVolatile te = new TestVolatile();
        ExecutorService exec = Executors.newCachedThreadPool();
        exec.execute(te);
        while (true) {
            int val = te.getVal();
            if (val%2!=0) {
                System.out.println(val);
                System.exit(0);
            }
        }
    }
}

