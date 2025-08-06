package com.flycode.flygenius.core;

import com.flycode.flygenius.ai.AiCodeGenerateService;
import com.flycode.flygenius.ai.model.CodeGenTypeEnum;
import com.flycode.flygenius.ai.model.HtmlCodeResult;
import com.flycode.flygenius.ai.model.MultiFileCodeResult;
import com.flycode.flygenius.core.parser.CoreParserExecutor;
import com.flycode.flygenius.core.saver.CodeFileSaverExecutor;
import com.flycode.flygenius.exception.BusinessException;
import com.flycode.flygenius.exception.ErrorCode;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;

/**
 * 文件生成代码门面模式
 *
 * @author flycode
 */
@Service
@Slf4j
public class AiCodeGeneratorFacade {
    @Resource
    private AiCodeGenerateService aiCodeGenerateService;

    /**
     * 生成代码，并保存到本地
     *
     * @param userMessage
     * @param codeGenType
     * @return
     */
    public File generatorAndSaveFile(String userMessage, CodeGenTypeEnum codeGenType, long appId) {
        if (codeGenType == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return switch (codeGenType) {
            case HTML -> {
                HtmlCodeResult generateHtmlCode = aiCodeGenerateService.generateHtmlCode(userMessage);
                yield CodeFileSaverExecutor.saveCode(CodeGenTypeEnum.HTML, generateHtmlCode, appId);
            }
            case MULTI_FILE -> {
                MultiFileCodeResult generatedMutlHtmlCode = aiCodeGenerateService.generateMutlHtmlCode(userMessage);
                yield CodeFileSaverExecutor.saveCode(CodeGenTypeEnum.MULTI_FILE, generatedMutlHtmlCode, appId);
            }
            default -> throw new BusinessException(ErrorCode.PARAMS_ERROR, "不支持的类型");
        };
    }


    /**
     * 流式输出和保存文件
     *
     * @param userMessage
     * @param codeGenType
     * @return
     */
    public Flux<String> generatorAndSaveFileStream(String userMessage, CodeGenTypeEnum codeGenType,long appId) {
        if (codeGenType == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return switch (codeGenType) {
            case HTML -> {
                Flux<String> htmlCodeStream = aiCodeGenerateService.generateMutlHtmlCodeStream(userMessage);
                yield processCodeStream(htmlCodeStream, CodeGenTypeEnum.HTML,appId);
            }
            case MULTI_FILE -> {
                Flux<String> mutlHtmlCodeStream = aiCodeGenerateService.generateMutlHtmlCodeStream(userMessage);
                yield processCodeStream(mutlHtmlCodeStream, CodeGenTypeEnum.MULTI_FILE,appId);
            }
            default -> throw new BusinessException(ErrorCode.PARAMS_ERROR, "不支持的类型");
        };
    }

    /**
     * @param codeStream
     * @param codeGenType
     * @return
     */
    public Flux<String> processCodeStream(Flux<String> codeStream, CodeGenTypeEnum codeGenType,long appId) {
        StringBuilder codeBuilder = new StringBuilder();
        return codeStream.doOnNext(chunk -> {
            codeBuilder.append(chunk);
        }).doOnComplete(() -> {
            try {
                String completeCode = codeBuilder.toString();
                // 执行器保存代码
                Object parserObj = CoreParserExecutor.parserObj(completeCode, codeGenType);
                File saveDir = CodeFileSaverExecutor.saveCode(codeGenType, parserObj,appId);
                log.info("文件保存路径:{}", saveDir);
            } catch (Exception e) {
                log.error("生成代码失败", e);
            }
        });
    }

}
