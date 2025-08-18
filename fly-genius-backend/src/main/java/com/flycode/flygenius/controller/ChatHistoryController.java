package com.flycode.flygenius.controller;

import com.flycode.flygenius.annotation.AuthCheck;
import com.flycode.flygenius.common.BaseResponse;
import com.flycode.flygenius.common.ResultUtils;
import com.flycode.flygenius.entity.constants.UserConstant;
import com.flycode.flygenius.entity.model.User;
import com.flycode.flygenius.entity.request.chatHistory.ChatHistoryRequest;
import com.flycode.flygenius.exception.ErrorCode;
import com.flycode.flygenius.exception.ThrowUtils;
import com.flycode.flygenius.service.impl.UserServiceImpl;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.flycode.flygenius.entity.model.ChatHistory;
import com.flycode.flygenius.service.ChatHistoryService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 对话历史 控制层。
 *
 * @author <a href="https://flycode.icu/">程序员飞云</a>
 */
@RestController
@RequestMapping("/chatHistory")
public class ChatHistoryController {

    @Autowired
    private ChatHistoryService chatHistoryService;
    @Resource
    private UserServiceImpl userService;

    /**
     * 查询某个应用下的对话历史
     *
     * @param appId
     * @param pageSize
     * @param pageNum
     * @param lastCreateTime
     * @param request
     * @return
     */
    @GetMapping("/app/{appId}")
    public BaseResponse<Page<ChatHistory>> listChatHistoryByPage(
            @PathVariable Long appId,
            @RequestParam(required = false) int pageSize,
            @RequestParam(required = false, defaultValue = "1") int pageNum,
            @RequestParam(required = false) LocalDateTime lastCreateTime,
            HttpServletRequest request
    ) {
        User currentLoginUser = userService.getCurrentLoginUser(request);
        Page<ChatHistory> chatHistoryPage = chatHistoryService.listChatHistoryByPage(appId, pageSize, pageNum, lastCreateTime, currentLoginUser);
        return ResultUtils.success(chatHistoryPage);
    }

    /**
     * 分页查询对话历史, 需要管理员权限
     *
     * @param chatHistoryRequest
     * @return
     */
    @PostMapping("/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<ChatHistory>> listByPage(@RequestBody ChatHistoryRequest chatHistoryRequest) {
        ThrowUtils.throwIf(chatHistoryRequest == null, ErrorCode.PARAMS_ERROR);
        int pageNum = chatHistoryRequest.getPageNum();
        int pageSize = chatHistoryRequest.getPageSize();
        QueryWrapper queryWrapper = chatHistoryService.getQueryWrapper(chatHistoryRequest);
        Page<ChatHistory> page = chatHistoryService.page(new Page<>(pageNum, pageSize), queryWrapper);
        return ResultUtils.success(page);
    }


}
