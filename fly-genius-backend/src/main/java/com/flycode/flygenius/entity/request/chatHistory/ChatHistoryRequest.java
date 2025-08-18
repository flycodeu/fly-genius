package com.flycode.flygenius.entity.request.chatHistory;

import com.flycode.flygenius.common.PageRequest;
import com.mybatisflex.annotation.Column;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 聊天记录查询请求
 *
 * @author flycode
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ChatHistoryRequest extends PageRequest implements Serializable {

    private static final long serialVersionUID = 8260314004752466253L;

    private Long id;

    /**
     * 消息
     */
    private String message;

    /**
     * user/ai
     */
    private String messageType;

    /**
     * 应用id
     */
    private Long appId;

    /**
     * 创建用户id
     */
    private Long userId;

    /**
     * 最后一次创建时间
     */
    private LocalDateTime lastCreateTime;

}
