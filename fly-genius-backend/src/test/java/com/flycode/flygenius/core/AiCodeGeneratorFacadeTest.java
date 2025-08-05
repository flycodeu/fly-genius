package com.flycode.flygenius.core;

import com.flycode.flygenius.ai.model.CodeGenTypeEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("local")
@Slf4j
class AiCodeGeneratorFacadeTest {
    @Resource
    private AiCodeGeneratorFacade aiCodeGeneratorFacade;

    @Test
    public void testSaveFile() {
        String userMessage = "请生成个人博客";
        File file = aiCodeGeneratorFacade.generatorAndSaveFile(userMessage, CodeGenTypeEnum.MULTI_FILE);
        log.info("file: {}", file.getAbsolutePath());
    }

    @Test
    public void testSaveFileStream() {
        String userMessage = "请生成登录界面，代码简短，需要20行内";
        Flux<String> fileStream = aiCodeGeneratorFacade.generatorAndSaveFileStream(userMessage, CodeGenTypeEnum.HTML);
        List<String> block = fileStream.collectList().block();
        log.info("fileStream: {}", block);
    }
}