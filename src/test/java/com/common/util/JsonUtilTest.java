package com.common.util;

import com.alibaba.fastjson.JSONObject;
import com.starter.StarterApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author ding
 * @date 3/21/2020
 */
@SpringBootTest(classes = StarterApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JsonUtilTest {
    @Test
    public void testParseObj() {
        String str = "{\"ext\":\".groovy\",\"patterns\":[\"\\\\w*[Tt].groovy\",\"[Tt]\\\\w*.groovy\"]}";
        JSONObject obj = JsonUtil.parseObj(str);
        Assertions.assertNotNull(obj);

        String ret = JsonUtil.toStr(obj);
        Assertions.assertTrue(str.equals(ret));
    }
}
