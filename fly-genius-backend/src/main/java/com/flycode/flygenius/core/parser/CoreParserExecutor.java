package com.flycode.flygenius.core.parser;

import com.flycode.flygenius.ai.model.CodeGenTypeEnum;
import com.flycode.flygenius.exception.BusinessException;
import com.flycode.flygenius.exception.ErrorCode;

/**
 * 核心解析器执行器，根据不同的类型执行不同的解析器
 *
 * @author flycode
 */
public class CoreParserExecutor {

    private static final HtmlCodeParser HTML_CODE_PARSER = new HtmlCodeParser();

    private static final MutiHtmlCodeParser MUTI_HTML_CODE_PARSER = new MutiHtmlCodeParser();

    /**
     * 根据不同的类型执行不同的解析器
     *
     * @param codeContent 代码内容
     * @param codeGenType 代码类型
     * @return 解析结果
     */
    public static Object parserObj(String codeContent, CodeGenTypeEnum codeGenType) {
        switch (codeGenType) {
            case HTML -> {
                return HTML_CODE_PARSER.parse(codeContent);
            }
            case MULTI_FILE -> {
                return MUTI_HTML_CODE_PARSER.parse(codeContent);
            }
            default -> throw new BusinessException(ErrorCode.PARAMS_ERROR, "不支持的类型");
        }
    }
}
