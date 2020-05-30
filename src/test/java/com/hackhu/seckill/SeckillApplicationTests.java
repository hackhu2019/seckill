package com.hackhu.seckill;

import com.hackhu.seckill.utils.EncodeUtil;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;


class SeckillApplicationTests {
    @Test
    void test() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        System.out.printf(EncodeUtil.EncodeByMd5("admin123"));
    }

}
