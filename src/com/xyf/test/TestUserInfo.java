package com.xyf.test;

import com.xyf.model.UserInfo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * 执行结果：107 24
 * 说明JDK序列化机制编码后的二进制数组居然是二进制编码的5倍多。
 * 说明序列化后的码流太大。
 * Created by xuyifei01 on 2015/3/13.
 */
public class TestUserInfo {
    public static void main(String[] args) {
        UserInfo info = new UserInfo();
        info.buildUserID(100).buildUserName("welcome to Netty");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream os = new ObjectOutputStream(bos);
            os.writeObject(info);
            os.flush();
            os.close();
            byte[] b = bos.toByteArray();
            System.out.println(b.length);
            bos.close();
            System.out.println(info.codeC().length);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
