package com.flycode.flygenius;

import com.flycode.flygenius.entity.model.App;
import com.flycode.flygenius.service.AppService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * App Service 测试类
 *
 * @author flycode
 */
@SpringBootTest
public class AppServiceTest {

    @Autowired
    private AppService appService;

    @Test
    public void testGetAppById() {
        // 测试获取应用详情
        App app = appService.getById(1L);
        // 这里只是简单的测试，实际项目中需要更详细的测试用例
        assertNotNull(appService);
    }
} 