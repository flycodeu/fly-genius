package com.flycode.flygenius.core.saver;

import com.flycode.flygenius.ai.model.CodeGenTypeEnum;
import com.flycode.flygenius.ai.model.HtmlCodeResult;
import com.flycode.flygenius.exception.BusinessException;
import com.flycode.flygenius.exception.ErrorCode;

import java.io.File;

/**
 * HTML代码文件保存模板
 *
 * @author flycode
 */
public class HtmlCodeFileSaverTemplate extends CodeFileSaverTemplate<HtmlCodeResult> {
    @Override
    protected void validInput(HtmlCodeResult input) {
        super.validInput(input);
        if (input.getHtmlCode() == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"输入参数不能为空");
        }
    }

    @Override
    protected CodeGenTypeEnum getCodeGenType() {
        return CodeGenTypeEnum.HTML;
    }

    @Override
    protected void saveCode(HtmlCodeResult result, String baseDirPath) {
        writeContentToFile("index.html", baseDirPath, result.getHtmlCode());
    }
}
