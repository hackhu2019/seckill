package com.hackhu.seckill;

import org.junit.jupiter.api.Test;
import org.apache.commons.lang.StringUtils;
import org.springframework.boot.test.context.SpringBootTest;


class SeckillApplicationTests {
    private String encode(String channel, String clientId, String appVersionCode) {
        String suffix = "GZZS";
        String priKey = channel + clientId + appVersionCode;
        if (StringUtils.length(priKey) > 16) {
            priKey = StringUtils.substring(priKey, 0, 16);
        } else {
            priKey =StringUtils.substring(priKey + suffix, 0, 16);
        }
        StringBuffer sb = new StringBuffer(priKey);
        priKey = sb.reverse() + "";
        return priKey;
    }
    @Test
    void test() {
        SeckillApplicationTests tests = new SeckillApplicationTests();
        String channel = "00001", clientId = "9687672", appVersionCode = "145";
        System.out.printf(tests.encode(channel, clientId, appVersionCode));
    }

}
