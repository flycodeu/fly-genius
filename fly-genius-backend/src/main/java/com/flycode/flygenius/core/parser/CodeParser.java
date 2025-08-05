package com.flycode.flygenius.core.parser;

/**
 * 策略模式实现代码解析AI响应文字
 *
 * @param <T>
 */
public interface CodeParser<T> {
    /**
     * 解析AI响应文字
     *
     * @param codeContent
     * @return 返回单个Html和多个Html、CSS、JS代码
     */
    T parse(String codeContent);
}
