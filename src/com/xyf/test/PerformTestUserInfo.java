package com.xyf.test;

import com.xyf.model.UserInfo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;

/**
 * 执行结果:
 *
 *  6002ms
    ===============================================
    253ms
 * Created by xuyifei01 on 2015/3/13.
 */
public class PerformTestUserInfo {
    public static void main(String[] args) throws IOException {
        UserInfo info = new UserInfo();
        info.buildUserID(100).buildUserName("Welcome to Netty");
        int loop = 1000000;
        ByteArrayOutputStream bos = null;
        long startTime = System.currentTimeMillis();
        ObjectOutputStream os = null;

        for (int i = 0; i < loop; i++) {
            bos = new ByteArrayOutputStream();
            os = new ObjectOutputStream(bos);
            os.writeObject(info);
            os.close();
            byte[] b = bos.toByteArray();
            bos.close();
        }
        long endTime = System.currentTimeMillis();
        System.out.println(endTime - startTime + "ms");
        System.out.println("===============================================");
        startTime = System.currentTimeMillis();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        for (int i=0;i<loop;i++) {
            byte[] b =info.codeC2(buffer);
        }
        endTime  = System.currentTimeMillis();

        System.out.println(endTime -startTime + "ms");
    }
}
