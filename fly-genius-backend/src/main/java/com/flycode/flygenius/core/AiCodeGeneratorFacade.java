package com.flycode.flygenius.core;

import com.flycode.flygenius.ai.AiCodeGenerateService;
import com.flycode.flygenius.ai.model.CodeGenTypeEnum;
import com.flycode.flygenius.ai.model.HtmlCodeResult;
import com.flycode.flygenius.ai.model.MultiFileCodeResult;
import com.flycode.flygenius.exception.BusinessException;
import com.flycode.flygenius.exception.ErrorCode;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * 文件生成代码门面模式
 *
 * @author flycode
 */
@Service
public class AiCodeGeneratorFacade {
    @Resource
    private AiCodeGenerateService aiCodeGenerateService;

    public File generatorAndSaveFile(String userMessage, CodeGenTypeEnum codeGenType) {
        if (codeGenType == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return switch (codeGenType) {
            case HTML -> generateHtmlCode(userMessage);
            case MULTI_FILE -> generateMutlHtmlCode(userMessage);
            default -> throw new BusinessException(ErrorCode.PARAMS_ERROR, "不支持的类型");
        };
    }

    /**
     * 生成多个文件代码
     *
     * @param userMessage
     * @return
     */
    private File generateMutlHtmlCode(String userMessage) {
        MultiFileCodeResult generatedMutlHtmlCode = aiCodeGenerateService.generateMutlHtmlCode(userMessage);
        return CodeFileSaver.saveMultiFileCode(generatedMutlHtmlCode);
    }

    /**
     * 生成 HTML 代码
     *
     * @param userMessage
     * @return
     */
    private File generateHtmlCode(String userMessage) {
        HtmlCodeResult generateHtmlCode = aiCodeGenerateService.generateHtmlCode(userMessage);
        return CodeFileSaver.saveHtmlCode(generateHtmlCode);
    }
}
