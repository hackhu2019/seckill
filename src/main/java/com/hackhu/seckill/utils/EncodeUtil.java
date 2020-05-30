package com.hackhu.seckill.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * @author hackhu
 * @date 2020/5/25
 */
public class EncodeUtil {
    private final static String SALT = "hack-hu";
    public static String EncodeByMd5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        //确定计算方法
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        String encodeStr = Base64.getEncoder().encodeToString(md5.digest((str + SALT).getBytes("utf-8")));
        return encodeStr;
    }
}
