package com.flycode.flygenius.ai;

import com.flycode.flygenius.service.ChatHistoryService;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import dev.langchain4j.community.store.memory.chat.redis.RedisChatMemoryStore;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * AI服务工厂
 *
 * @author flycode
 */
@Slf4j
@Configuration
public class AiCodeGeneratorServiceFactory {

    @Resource
    private ChatModel chatModel;

    @Resource
    private StreamingChatModel streamingChatModel;

    @Resource
    private RedisChatMemoryStore redisChatMemoryStore;

    @Resource
    private ChatHistoryService chatHistoryService;
//    @Bean
//    public AiCodeGenerateService aiCodeGeneratorService() {
//        return AiServices.create(AiCodeGenerateService.class, chatModel);
//    }

    /**
     * 流式输出，默认使用0L的appId
     *
     * @return
     */
    @Bean
    public AiCodeGenerateService aiCodeGeneratorService() {
        return this.createAiCodeServiceByAppId(0L);
    }


    /**
     * AI 服务实例缓存
     * 缓存策略：
     * - 最大缓存 1000 个实例
     * - 写入后 30 分钟过期
     * - 访问后 10 分钟过期
     */
    private final Cache<Long, AiCodeGenerateService> serviceCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(Duration.ofMinutes(30))
            .expireAfterAccess(Duration.ofMinutes(10))
            .removalListener((key, value, cause) -> {
                log.debug("AI 服务实例被移除，appId: {}, 原因: {}", key, cause);
            })
            .build();

    /**
     * 根据 appId 获取服务（带缓存）
     */
    public AiCodeGenerateService getAiCodeGeneratorService(long appId) {
        return serviceCache.get(appId, this::createAiCodeServiceByAppId);
    }


    /**
     * 根据appId创建AI服务
     *
     * @param appId
     * @return
     */
    public AiCodeGenerateService createAiCodeServiceByAppId(Long appId) {
        MessageWindowChatMemory chatMemory = MessageWindowChatMemory.builder()
                .id(appId)
                .maxMessages(20)
                .chatMemoryStore(redisChatMemoryStore)
                .build();

        chatHistoryService.loadChatHistoryFromMemory(appId, chatMemory, 20);
        return AiServices.builder(AiCodeGenerateService.class)
                .chatModel(chatModel)
                .streamingChatModel(streamingChatModel)
                .chatMemory(chatMemory)
                .build();
    }
}
