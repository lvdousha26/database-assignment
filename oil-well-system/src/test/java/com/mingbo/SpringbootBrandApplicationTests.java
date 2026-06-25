package com.mingbo;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Disabled("需要数据库环境")
@SpringBootTest(classes = OilWellApplication.class)
class SpringbootBrandApplicationTests {

    @Test
    void contextLoads() {
    }

}
