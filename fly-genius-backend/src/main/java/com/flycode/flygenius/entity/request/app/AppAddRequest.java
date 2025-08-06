package com.flycode.flygenius.entity.request.app;

import lombok.Data;

import java.io.Serializable;

@Data
public class AppAddRequest implements Serializable {

    /**
     * 初始化提示词
     */
    private String initPrompt;

    private static final long serialVersionUID = 1L;
}
