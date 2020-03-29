package com.starter.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import springfox.documentation.service.ApiInfo;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * @author ding
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SwaggerConfigTest {
    @Autowired
    SwaggerConfig swaggerConfig;

    @Test
    public void testDocket() {
        // Mock
        SwaggerConfig swaggerConfig = spy(this.swaggerConfig);
        when(swaggerConfig.apiInfo()).thenReturn(mock(ApiInfo.class));

        // Verify
        Assertions.assertNotNull(swaggerConfig.docket());
    }

    @Test
    public void testApiInfo() {
        Assertions.assertNotNull(swaggerConfig.apiInfo());
    }
}
