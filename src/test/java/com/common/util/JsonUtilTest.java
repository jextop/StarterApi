package com.common.util;

import com.alibaba.fastjson.JSONObject;
import com.starter.StarterApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = StarterApplication.class)
public class JsonUtilTest {
    @Test
    public void testJsonUtil() {
        String str = "{\"ext\":\".groovy\",\"patterns\":[\"\\\\w*[Tt].groovy\",\"[Tt]\\\\w*.groovy\"]}";
        JSONObject obj = JsonUtil.parseObj(str);

        String ret = JsonUtil.toStr(obj);
        LogUtil.info(ret, str);
        Assertions.assertTrue(str.equals(ret));
    }
}
