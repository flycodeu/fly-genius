package com.flycode.flygenius.service;

import com.mybatisflex.core.service.IService;
import com.flycode.flygenius.entity.model.ChatHistory;

/**
 * 对话历史 服务层。
 *
 * @author <a href="https://flycode.icu/">程序员飞云</a>
 */
public interface ChatHistoryService extends IService<ChatHistory> {

    /**
     * 添加对话历史
     *
     * @param appId
     * @param message
     * @param userId
     * @param messageType
     * @return
     */
    boolean addChatHistory(Long appId, String message, Long userId, String messageType);

    /**
     * 根据appId删除对话历史
     *
     * @param appId
     * @return
     */
    boolean deleteChatHistoryByAppId(Long appId);
}
