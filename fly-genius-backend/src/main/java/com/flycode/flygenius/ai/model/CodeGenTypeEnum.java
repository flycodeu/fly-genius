package com.flycode.flygenius.ai.model;

import lombok.Getter;

/**
 * 代码生成类型枚举
 *
 * @author flycode
 */
@Getter
public enum CodeGenTypeEnum {
    HTML("原生Html代码", "html"),
    MULTI_FILE("多个文件代码", "multi-file");

    private String text;

    private String value;

    CodeGenTypeEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    public static CodeGenTypeEnum getEnumByValue(String value) {
        if (value == null) {
            return null;
        }
        for (CodeGenTypeEnum item : CodeGenTypeEnum.values()) {
            if (item.value.equals(value)) {
                return item;
            }
        }
        return null;
    }
}
