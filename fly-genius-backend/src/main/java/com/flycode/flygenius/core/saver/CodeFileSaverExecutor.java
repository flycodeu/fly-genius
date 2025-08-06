package com.flycode.flygenius.core.saver;

import com.flycode.flygenius.ai.model.CodeGenTypeEnum;
import com.flycode.flygenius.ai.model.HtmlCodeResult;
import com.flycode.flygenius.ai.model.MultiFileCodeResult;

import java.io.File;

import static com.flycode.flygenius.ai.model.CodeGenTypeEnum.MULTI_FILE;

public class CodeFileSaverExecutor {
    private static final HtmlCodeFileSaverTemplate htmlCodeFileSaverTemplate = new HtmlCodeFileSaverTemplate();
    private static final MultCodeFileSaverTemplate multCodeFileSaverTemplate = new MultCodeFileSaverTemplate();

    public static File saveCode(CodeGenTypeEnum codeGenType, Object result, long appId) {
        return switch (codeGenType) {
            case HTML -> htmlCodeFileSaverTemplate.saveCode((HtmlCodeResult) result, appId);
            case MULTI_FILE -> multCodeFileSaverTemplate.saveCode((MultiFileCodeResult) result, appId);
            default -> throw new IllegalArgumentException("Invalid code generation type: " + codeGenType);
        };
    }
}
