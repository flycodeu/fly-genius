package com.flycode.flygenius;

import com.flycode.flygenius.ai.AiCodeGenerateService;
import com.flycode.flygenius.ai.AiCodeGeneratorServiceFactory;
import com.flycode.flygenius.ai.model.CodeGenTypeEnum;
import com.flycode.flygenius.ai.model.HtmlCodeResult;
import com.flycode.flygenius.ai.model.MultiFileCodeResult;
import com.flycode.flygenius.core.AiCodeGeneratorFacade;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;

import java.util.List;

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

    @Resource
    private AiCodeGeneratorFacade aiCodeGeneratorFacade;

    @Test
    void generateVueProjectCodeStream() {
        Flux<String> codeStream = aiCodeGeneratorFacade.generatorAndSaveFileStream(
                "简单的任务记录网站，总代码量不超过 200 行",
                CodeGenTypeEnum.VUE_PROJECT, 1L);
        // 阻塞等待所有数据收集完成
        List<String> result = codeStream.collectList().block();
        // 验证结果
        Assertions.assertNotNull(result);
        String completeContent = String.join("", result);
        Assertions.assertNotNull(completeContent);
    }


}
