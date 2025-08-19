package com.flycode.flygenius.ai;


import com.flycode.flygenius.ai.model.HtmlCodeResult;
import com.flycode.flygenius.ai.model.MultiFileCodeResult;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import reactor.core.publisher.Flux;

/**
 * AI服务接口
 */
public interface AiCodeGenerateService {

    /**
     * 从指定位置文件中获取系统提示语，生成单个HTML代码片段
     * @param userMessage
     * @return
     */
    @SystemMessage(fromResource = "prompt/codegen-html-system-prompt.txt")
    HtmlCodeResult generateHtmlCode(String userMessage);

    /**
     * 从指定位置文件中获取系统提示语，生成多个HTML代码片段
     * @param userMessage
     * @return
     */
    @SystemMessage(fromResource = "prompt/codegen-muti-html-system-prompt.txt")
    MultiFileCodeResult generateMutlHtmlCode(String userMessage);


    /**
     * 从指定位置文件中获取系统提示语，生成单个HTML代码片段，流式输出
     * @param userMessage
     * @return
     */
    @SystemMessage(fromResource = "prompt/codegen-html-system-prompt.txt")
    Flux<String> generateHtmlCodeStream(String userMessage);

    /**
     * 从指定位置文件中获取系统提示语，生成多个HTML代码片段,流式输出
     * @param userMessage
     * @return
     */
    @SystemMessage(fromResource = "prompt/codegen-muti-html-system-prompt.txt")
    Flux<String> generateMutlHtmlCodeStream(String userMessage);


    /**
     * 生成 Vue 项目代码（流式）
     *
     * @param userMessage 用户消息
     * @return 生成过程的流式响应
     */
    @SystemMessage(fromResource = "prompt/codegen-vue-project-system-prompt.txt")
    Flux<String> generateVueProjectCodeStream(@MemoryId long appId, @UserMessage String userMessage);

}