package com.flycode.flygenius.ai;


import com.flycode.flygenius.ai.model.HtmlCodeResult;
import com.flycode.flygenius.ai.model.MultiFileCodeResult;
import dev.langchain4j.service.SystemMessage;

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
}