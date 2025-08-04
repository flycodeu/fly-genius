package com.flycode.flygenius;

import com.flycode.flygenius.ai.AiCodeGenerateService;
import com.flycode.flygenius.ai.model.HtmlCodeResult;
import com.flycode.flygenius.ai.model.MultiFileCodeResult;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("local")
@Slf4j
class FlyGeniusApplicationTests {
    @Resource
    private AiCodeGenerateService aiCodeGenerateService;
    @Test
    void contextLoads() {
        HtmlCodeResult generateHtmlCode = aiCodeGenerateService.generateHtmlCode("给我生成一篇博客");
        log.info("生成代码的描述：{}", generateHtmlCode.getDescription());
        log.info(generateHtmlCode.getHtmlCode());
        MultiFileCodeResult generatedMutlHtmlCode = aiCodeGenerateService.generateMutlHtmlCode("给我生成一篇博客");
        log.info("生成代码的描述：{}", generatedMutlHtmlCode.getDescription());
        log.info(generatedMutlHtmlCode.getHtmlCode());
        log.info(generatedMutlHtmlCode.getCssCode());
        log.info(generatedMutlHtmlCode.getJsCode());
    }

}
