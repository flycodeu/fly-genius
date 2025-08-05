package com.flycode.flygenius.core;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.flycode.flygenius.ai.model.CodeGenTypeEnum;
import com.flycode.flygenius.ai.model.HtmlCodeResult;
import com.flycode.flygenius.ai.model.MultiFileCodeResult;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * 文件根据html生成对应文件
 *
 * @author flycode
 */
@Deprecated
public class CodeFileSaver {
    //  保存文件的目录
    public static final String FILE_SAVE_DIR = System.getProperty("user.dir") + "/tmp/ai_code_result";

    /**
     * 根据生成类型生成文件唯一标识，使用雪花算法
     *
     * @param bizType 代码类型
     * @return 返回目录
     */
    public static String buildUniqueFileDir(String bizType) {
        String uniqueDirName = StrUtil.format("{}_{}", bizType, IdUtil.getSnowflakeNextIdStr());
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
    public static File writeContentToFile(String fileName, String dirPath, String content) {
        String filePath = dirPath + File.separator + fileName;
        return FileUtil.writeString(content, filePath, StandardCharsets.UTF_8);
    }


    /**
     * 生成单个html文件
     *
     * @param result html代码
     * @return 文件
     */
    public static File saveHtmlCode(HtmlCodeResult result) {
        String dirPath = buildUniqueFileDir(CodeGenTypeEnum.HTML.getValue());
        writeContentToFile("index.html", dirPath, result.getHtmlCode());
        return new File(dirPath);
    }

    /**
     * 生成多个文件
     *
     * @param result 多个文件代码
     * @return 文件
     */
    public static File saveMultiFileCode(MultiFileCodeResult result) {
        String dirPath = buildUniqueFileDir(CodeGenTypeEnum.MULTI_FILE.getValue());
        writeContentToFile("index.html", dirPath, result.getHtmlCode());
        writeContentToFile("style.css", dirPath, result.getCssCode());
        writeContentToFile("script.js", dirPath, result.getJsCode());
        return new File(dirPath);
    }
}
