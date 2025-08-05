package com.flycode.flygenius.ai;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AI服务工厂
 *
 * @author flycode
 */
@Configuration
public class AiCodeGeneratorServiceFactory {

    @Resource
    private ChatModel chatModel;

    @Resource
    private StreamingChatModel streamingChatModel;

//    @Bean
//    public AiCodeGenerateService aiCodeGeneratorService() {
//        return AiServices.create(AiCodeGenerateService.class, chatModel);
//    }

    /**
     * 流式输出
     *
     * @return
     */
    @Bean
    public AiCodeGenerateService aiCodeGeneratorService() {
        return AiServices.builder(AiCodeGenerateService.class)
                .chatModel(chatModel)
                .streamingChatModel(streamingChatModel)
                .build();
    }
}
