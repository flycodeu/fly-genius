package com.flycode.flygenius.config;

import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 推理测试模型配置，用于创建vue项目
 * @author flycode
 */
@Configuration
@ConfigurationProperties(prefix = "langchain4j.open-ai.chat-model")
@Data
public class ReasoningStreamingChatModelConfig {

    private String baseUrl;

    private String apiKey;

    /**
     * 推理模型流式输出，目前仅支持deepseek-chat模型减少成本，创建Vue项目
     *
     * @return
     */
    @Bean
    public StreamingChatModel reasonStreamChatModel() {
        final String modelName = "deepseek-chat";
        final int maxToken = 8192;
//        final String modelName = "deepseek-reasoner";
//        final int maxToken = 32768;
        return OpenAiStreamingChatModel.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .maxTokens(maxToken)
                .modelName(modelName)
                .logRequests(true)
                .logResponses(true)
                .build();
    }
}
