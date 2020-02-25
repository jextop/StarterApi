package com.starter.http;

import com.common.util.LogUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class LocationServiceTest {
    @Autowired(required = false)
    LocationConfig locationConfig;

    @Autowired
    LocationService locationService;

    @Test
    public void testGetAddress() {
        Object ret = locationService.getAddress("218.192.3.42");
        LogUtil.info(ret);
        Assertions.assertTrue(ret != null || locationConfig == null);
    }
}
