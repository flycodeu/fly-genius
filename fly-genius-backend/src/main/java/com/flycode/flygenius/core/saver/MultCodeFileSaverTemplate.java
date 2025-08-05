package com.flycode.flygenius.core.saver;

import com.flycode.flygenius.ai.model.CodeGenTypeEnum;
import com.flycode.flygenius.ai.model.MultiFileCodeResult;
import com.flycode.flygenius.exception.BusinessException;
import com.flycode.flygenius.exception.ErrorCode;

public class MultCodeFileSaverTemplate extends CodeFileSaverTemplate<MultiFileCodeResult>{
    @Override
    protected void validInput(MultiFileCodeResult input) {
        super.validInput(input);
        if (input.getHtmlCode() == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"输入Html不能为空");
        }
    }

    @Override
    protected CodeGenTypeEnum getCodeGenType() {
        return CodeGenTypeEnum.MULTI_FILE;
    }

    @Override
    protected void saveCode(MultiFileCodeResult result, String baseDirPath) {
        writeContentToFile("index.html", baseDirPath, result.getHtmlCode());
        writeContentToFile("style.css", baseDirPath, result.getCssCode());
        writeContentToFile("script.js", baseDirPath, result.getJsCode());
    }
}
