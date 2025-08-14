package com.flycode.flygenius.service.impl;

import cn.hutool.core.util.StrUtil;
import com.flycode.flygenius.entity.enums.ChatHistoryMessageTypeEnum;
import com.flycode.flygenius.exception.ErrorCode;
import com.flycode.flygenius.exception.ThrowUtils;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.flycode.flygenius.entity.model.ChatHistory;
import com.flycode.flygenius.mapper.ChatHistoryMapper;
import com.flycode.flygenius.service.ChatHistoryService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 对话历史 服务层实现。
 *
 * @author <a href="https://flycode.icu/">程序员飞云</a>
 */
@Service
public class ChatHistoryServiceImpl extends ServiceImpl<ChatHistoryMapper, ChatHistory> implements ChatHistoryService {

    @Override
    public boolean addChatHistory(Long appId, String message, Long userId, String messageType) {
        // 1. 校验参数
        ThrowUtils.throwIf(appId <= 0, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(StrUtil.isBlank(message), ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(StrUtil.isBlank(messageType), ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(userId <= 0 || userId == null, ErrorCode.PARAMS_ERROR);
        // 2. 验证类型
        ChatHistoryMessageTypeEnum messageTypeEnum = ChatHistoryMessageTypeEnum.getEnumByValue(messageType);
        ThrowUtils.throwIf(messageTypeEnum == null, ErrorCode.PARAMS_ERROR);
        // 3. 插入数据库
        ChatHistory chatHistory = ChatHistory.builder()
                .appId(appId)
                .message(message)
                .messageType(messageType)
                .userId(userId)
                .build();
        return this.save(chatHistory);
    }

    @Override
    public boolean deleteChatHistoryByAppId(Long appId) {
        return this.remove(new QueryWrapper().eq("appId", appId));
    }
}
