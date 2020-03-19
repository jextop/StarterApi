package com.starter.speech;

import com.common.util.LogUtil;
import com.common.util.MacUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class TulingServiceTest {
    @Autowired
    TulingService tulingService;

    @Test
    public void testChat() {
        /*
        {
          "intent": {
            "actionName": "",
            "code": 10037,
            "intentName": ""
          },
          "results": [
            {
              "groupType": 1,
              "resultType": "text",
              "values": {
                "text": "在外住酒店，还是得小心点好哦~"
              }
            }
          ]
        }
        */
        Object ret = tulingService.chat("你知道上海的天气吗？", null, MacUtil.gtMacAddr());
        LogUtil.info(ret);
        Assertions.assertNotNull(ret);
    }
}
