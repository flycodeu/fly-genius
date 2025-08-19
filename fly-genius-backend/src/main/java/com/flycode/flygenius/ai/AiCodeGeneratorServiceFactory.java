package com.flycode.flygenius.ai;

import com.flycode.flygenius.ai.model.CodeGenTypeEnum;
import com.flycode.flygenius.ai.tools.FileWriteTool;
import com.flycode.flygenius.service.ChatHistoryService;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.community.store.memory.chat.redis.RedisChatMemoryStore;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
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
    private StreamingChatModel openAiStreamingChatModel;

    @Resource
    private RedisChatMemoryStore redisChatMemoryStore;

    @Resource
    private ChatHistoryService chatHistoryService;

    @Resource
    private StreamingChatModel reasonStreamChatModel;

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
    private final Cache<String, AiCodeGenerateService> serviceCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(Duration.ofMinutes(30))
            .expireAfterAccess(Duration.ofMinutes(10))
            .removalListener((key, value, cause) -> {
                log.debug("AI 服务实例被移除，缓存key: {}, 原因: {}", key, cause);
            })
            .build();

    /**
     * 根据 appId 获取服务（带缓存）
     */
    public AiCodeGenerateService getAiCodeGeneratorService(long appId) {
        return getAiCodeGeneratorService(appId, CodeGenTypeEnum.HTML);
    }


    /**
     * 根据 appId、codeType 获取服务（带缓存）
     */
    public AiCodeGenerateService getAiCodeGeneratorService(long appId, CodeGenTypeEnum codeGenType) {
        String cacheKey = buildCacheKey(appId, codeGenType);
        return serviceCache.get(cacheKey, (key) -> createAiCodeServiceByAppIdAndCodeType(appId, codeGenType));
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
                .streamingChatModel(openAiStreamingChatModel)
                .chatMemory(chatMemory)
                .build();
    }


    /**
     * 根据appId和代码生成类型创建AI服务
     *
     * @param appId
     * @param codeGenType
     * @return
     */
    public AiCodeGenerateService createAiCodeServiceByAppIdAndCodeType(Long appId, CodeGenTypeEnum codeGenType) {
        MessageWindowChatMemory chatMemory = MessageWindowChatMemory.builder()
                .id(appId)
                .maxMessages(20)
                .chatMemoryStore(redisChatMemoryStore)
                .build();
        // 数据库加载历史记录
        chatHistoryService.loadChatHistoryFromMemory(appId, chatMemory, 20);

        // 区分不同的代码生成类型
        return switch (codeGenType) {
            case HTML, MULTI_FILE -> AiServices.builder(AiCodeGenerateService.class)
                    .chatModel(chatModel)
                    .streamingChatModel(openAiStreamingChatModel)
                    .chatMemory(chatMemory)
                    .build();
            case VUE_PROJECT -> AiServices.builder(AiCodeGenerateService.class)
                    .chatModel(chatModel)
                    // 切换模型
                    .streamingChatModel(reasonStreamChatModel)
                    // 使用工具
                    .tools(new FileWriteTool())
                    .chatMemoryProvider(memoryId -> chatMemory)
                    // 找不倒对应工具，返回数据
                    .hallucinatedToolNameStrategy(toolExecutionRequest -> ToolExecutionResultMessage.from(toolExecutionRequest, "Error :there is no tool called " + toolExecutionRequest.name().toString()))
                    .build();
            default -> throw new IllegalArgumentException("不支持的代码生成类型");
        };
    }


    private String buildCacheKey(Long appId, CodeGenTypeEnum codeGenType) {
        return appId + "_" + codeGenType;
    }
}
