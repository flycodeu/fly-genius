package com.flycode.flygenius.core.saver;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.flycode.flygenius.ai.model.CodeGenTypeEnum;
import com.flycode.flygenius.exception.BusinessException;
import com.flycode.flygenius.exception.ErrorCode;

import java.io.File;
import java.nio.charset.StandardCharsets;

public abstract class CodeFileSaverTemplate<T> {

    protected static final String FILE_SAVE_DIR = System.getProperty("user.dir") + "/tmp/ai_code_result";

    public final File saveCode(T result) {
        // 验证输入参数
        validInput(result);
        // 生成文件地址目录
        String baseDirPath = buildUniqueFileDir();
        // 保存代码
        saveCode(result, baseDirPath);
        // 返回保存的目录
        return new File(baseDirPath);
    }

    /**
     * 验证输入参数
     *
     * @param input
     */
    protected  void validInput(T input){
        if (input == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"输入参数不能为空");
        }
    } ;

    /**
     * 根据生成类型生成文件唯一标识，使用雪花算法
     *
     * @return 返回目录
     */
    protected  String buildUniqueFileDir() {
        // 从子类获取生成类型
        String codeType = getCodeGenType().getValue();
        String uniqueDirName = StrUtil.format("{}_{}", codeType, IdUtil.getSnowflakeNextIdStr());
        String dirPath = FILE_SAVE_DIR + File.separator + uniqueDirName;
        FileUtil.mkdir(dirPath);
        return dirPath;
    }


    /**
     * 生成文件
     *
     * @param fileName 文件名
     * @param dirPath  目录
     * @param content  内容
     * @return 文件
     */
    public final void writeContentToFile(String fileName, String dirPath, String content) {
        String filePath = dirPath + File.separator + fileName;
        FileUtil.writeString(content, filePath, StandardCharsets.UTF_8);
    }

    /**
     * 获取代码类型
     *
     * @return
     */
    protected abstract CodeGenTypeEnum getCodeGenType();

    /**
     * 保存代码
     *
     * @param result
     * @param baseDirPath
     * @return
     */
    protected abstract void saveCode(T result, String baseDirPath);
}
