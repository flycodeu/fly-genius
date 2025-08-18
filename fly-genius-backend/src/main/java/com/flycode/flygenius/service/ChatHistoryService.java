package com.flycode.flygenius.service;

import com.flycode.flygenius.entity.model.User;
import com.flycode.flygenius.entity.request.chatHistory.ChatHistoryRequest;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.flycode.flygenius.entity.model.ChatHistory;

import java.time.LocalDateTime;

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

    /**
     * 获取查询包装器
     *
     * @param chatHistoryRequest
     * @return
     */
    QueryWrapper getQueryWrapper(ChatHistoryRequest chatHistoryRequest);

    /**
     * 分页获取对话历史
     *
     * @param appId
     * @param pageSize
     * @param pageNum
     * @param lastCreateTime
     * @param loginUser
     * @return
     */
    Page<ChatHistory> listChatHistoryByPage(Long appId, int pageSize, int pageNum, LocalDateTime lastCreateTime, User loginUser);
}
