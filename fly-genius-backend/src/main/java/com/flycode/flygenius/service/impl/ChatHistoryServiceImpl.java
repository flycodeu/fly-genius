package com.flycode.flygenius.service.impl;

import cn.hutool.core.util.StrUtil;
import com.flycode.flygenius.entity.constants.UserConstant;
import com.flycode.flygenius.entity.enums.ChatHistoryMessageTypeEnum;
import com.flycode.flygenius.entity.model.App;
import com.flycode.flygenius.entity.model.User;
import com.flycode.flygenius.entity.request.chatHistory.ChatHistoryRequest;
import com.flycode.flygenius.exception.BusinessException;
import com.flycode.flygenius.exception.ErrorCode;
import com.flycode.flygenius.exception.ThrowUtils;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.flycode.flygenius.entity.model.ChatHistory;
import com.flycode.flygenius.mapper.ChatHistoryMapper;
import com.flycode.flygenius.service.ChatHistoryService;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 对话历史 服务层实现。
 *
 * @author <a href="https://flycode.icu/">程序员飞云</a>
 */
@Service
public class ChatHistoryServiceImpl extends ServiceImpl<ChatHistoryMapper, ChatHistory> implements ChatHistoryService {
    @Resource
    @Lazy
    private AppServiceImpl appService;

    @Override
    public boolean addChatHistory(Long appId, String message, Long userId, String messageType) {
        // 1. 校验参数
        ThrowUtils.throwIf(appId <= 0, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(StrUtil.isBlank(message), ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(StrUtil.isBlank(messageType), ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(userId == null || userId <= 0, ErrorCode.PARAMS_ERROR);
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
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR);
        return this.remove(new QueryWrapper().eq("appId", appId));
    }


    @Override
    public QueryWrapper getQueryWrapper(ChatHistoryRequest chatHistoryRequest) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        if (chatHistoryRequest == null) {
            return queryWrapper;
        }
        Long id = chatHistoryRequest.getId();
        String message = chatHistoryRequest.getMessage();
        String messageType = chatHistoryRequest.getMessageType();
        Long appId = chatHistoryRequest.getAppId();
        Long userId = chatHistoryRequest.getUserId();
        LocalDateTime lastCreateTime = chatHistoryRequest.getLastCreateTime();
        String sortField = chatHistoryRequest.getSortField();
        String sortOrder = chatHistoryRequest.getSortOrder();
        queryWrapper.eq(ChatHistory::getId, id)
                .like(ChatHistory::getMessage, message)
                .eq(ChatHistory::getMessageType, messageType)
                .eq(ChatHistory::getAppId, appId)
                .eq(ChatHistory::getUserId, userId);
        // 使用游标查询，小于当前时间的
        if (lastCreateTime != null) {
            queryWrapper.lt(ChatHistory::getCreateTime, lastCreateTime);
        }

        // 排序，时间倒序
        if (StrUtil.isNotBlank(sortField)) {
            queryWrapper.orderBy(sortField, "ascend".equals(sortOrder));
        } else {
            queryWrapper.orderBy(ChatHistory::getCreateTime, false);
        }
        return queryWrapper;
    }

    @Override
    public Page<ChatHistory> listChatHistoryByPage(Long appId, int pageSize, int pageNum, LocalDateTime lastCreateTime, User loginUser) {
        // 1. 校验参数
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "appId不能为空");
        ThrowUtils.throwIf(pageSize <= 0 || pageNum <= 0, ErrorCode.PARAMS_ERROR, "pageSize和pageNum不能小于0");
        ThrowUtils.throwIf(pageSize > 50, ErrorCode.PARAMS_ERROR, "pageSize不能大于50");
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        // 2. 权限校验，只有管理员或者应用创建者可以查询
        App app = appService.getById(appId);
        Long createUserId = app.getUserId();
        if (!UserConstant.ADMIN_ROLE.equals(loginUser.getUserRole()) && !createUserId.equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无查询权限");
        }
        // 3. 参数构建
        ChatHistoryRequest chatHistoryRequest = new ChatHistoryRequest();
        chatHistoryRequest.setAppId(appId);
        chatHistoryRequest.setLastCreateTime(lastCreateTime);
        QueryWrapper queryWrapper = getQueryWrapper(chatHistoryRequest);
        return this.page(new Page<>(pageNum, pageSize), queryWrapper);
    }
}
