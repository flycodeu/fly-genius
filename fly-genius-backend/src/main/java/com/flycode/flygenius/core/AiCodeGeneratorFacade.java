package com.flycode.flygenius.core;

import com.flycode.flygenius.ai.AiCodeGenerateService;
import com.flycode.flygenius.ai.model.CodeGenTypeEnum;
import com.flycode.flygenius.ai.model.HtmlCodeResult;
import com.flycode.flygenius.ai.model.MultiFileCodeResult;
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


    /**
     * 生成多个文件代码，完成流式输出后，保存到本地
     *
     * @param userMessage
     * @return
     */
    private Flux<String> generateMutlHtmlCodeStream(String userMessage) {
        Flux<String> result = aiCodeGenerateService.generateMutlHtmlCodeStream(userMessage);
        StringBuilder codeBuilder = new StringBuilder();
        return result.doOnNext(chunk -> {
                    codeBuilder.append(chunk);
                })
                .doOnComplete(() -> {
                    try {
                        String res = codeBuilder.toString();
                        MultiFileCodeResult multiFileCodeResult = CodeParser.parseMultiFileCode(res);
                        File file = CodeFileSaver.saveMultiFileCode(multiFileCodeResult);
                        log.info("生成代码成功：{}", file.getAbsolutePath());
                    } catch (Exception e) {
                        log.error("生成代码失败", e);
                    }
                });
    }

    /**
     * 生成 HTML 代码
     *
     * @param userMessage
     * @return
     */
    private Flux<String> generateHtmlCodeStream(String userMessage) {
        Flux<String> result = aiCodeGenerateService.generateHtmlCodeStream(userMessage);
        StringBuilder codeBuilder = new StringBuilder();
        return result.doOnNext(chunk -> {
            codeBuilder.append(chunk);
        }).doOnComplete(() -> {
                    try {
                        String res = codeBuilder.toString();
                        HtmlCodeResult htmlCodeResult = CodeParser.parseHtmlCode(res);
                        File file = CodeFileSaver.saveHtmlCode(htmlCodeResult);
                        log.info("生成代码成功：{}", file.getAbsolutePath());
                    } catch (Exception e) {
                        log.error("生成代码失败", e);
                    }
                }
        );
    }

    /**
     * 流式输出和保存文件
     *
     * @param userMessage
     * @param codeGenType
     * @return
     */
    public Flux<String> generatorAndSaveFileStream(String userMessage, CodeGenTypeEnum codeGenType) {
        if (codeGenType == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return switch (codeGenType) {
            case HTML -> generateHtmlCodeStream(userMessage);
            case MULTI_FILE -> generateMutlHtmlCodeStream(userMessage);
            default -> throw new BusinessException(ErrorCode.PARAMS_ERROR, "不支持的类型");
        };
    }

}
