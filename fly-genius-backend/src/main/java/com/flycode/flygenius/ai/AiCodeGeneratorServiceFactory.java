package com.flycode.flygenius.ai;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AI服务工厂
 * @author flycode
 */
@Configuration
public class AiCodeGeneratorServiceFactory {

    @Resource
    private ChatModel chatModel;

    @Bean
    public AiCodeGenerateService aiCodeGeneratorService() {
        return AiServices.create(AiCodeGenerateService.class, chatModel);
    }
}
