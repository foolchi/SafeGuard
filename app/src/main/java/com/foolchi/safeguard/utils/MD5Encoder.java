package com.foolchi.safeguard.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
/**
 * Created by foolchi on 6/21/14.
 * MD5加密
 */
public class MD5Encoder {
    public static String encode(String pwd) {
        try {
            // 拿到MD5加密对象
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            // 返回一个加密后的字节数组
            byte[] bytes = messageDigest.digest(pwd.getBytes());
            StringBuffer sb = new StringBuffer();
            String tmp;
            for (int i = 0; i < bytes.length; i++){
                // 把字节转换为16进制字符串
                tmp = Integer.toHexString(0xff & bytes[i]);
                // 长度为1需要补0
                if (tmp.length() == 1){
                    sb.append("0" + tmp);
                }
                else {
                    sb.append(tmp);
                }
            }
            return sb.toString();
        }
        catch (NoSuchAlgorithmException e){
            throw new RuntimeException("No such algorithm" + e);
        }
    }
}
