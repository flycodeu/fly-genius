package com.flycode.flygenius.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.flycode.flygenius.annotation.AuthCheck;
import com.flycode.flygenius.common.BaseResponse;
import com.flycode.flygenius.common.DeleteRequest;
import com.flycode.flygenius.common.ResultUtils;
import com.flycode.flygenius.entity.constants.AppConstant;
import com.flycode.flygenius.entity.constants.UserConstant;
import com.flycode.flygenius.entity.request.app.AppAddRequest;
import com.flycode.flygenius.entity.request.app.AppAdminUpdateRequest;
import com.flycode.flygenius.entity.request.app.AppQueryRequest;
import com.flycode.flygenius.entity.request.app.AppUpdateRequest;
import com.flycode.flygenius.entity.vo.AppVo;
import com.flycode.flygenius.exception.BusinessException;
import com.flycode.flygenius.exception.ErrorCode;
import com.flycode.flygenius.exception.ThrowUtils;
import com.flycode.flygenius.service.AppService;
import com.flycode.flygenius.service.UserService;
import com.flycode.flygenius.entity.model.App;
import com.flycode.flygenius.entity.model.User;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 应用 控制层。
 *
 * @author flycode
 */
@RestController
@RequestMapping("/app")
public class AppController {

    @Autowired
    private AppService appService;

    @Autowired
    private UserService userService;

    /**
     * 用户创建应用
     *
     * @param appAddRequest 应用创建请求
     * @param request       请求对象
     * @return 创建成功应用id
     */
    @PostMapping("/create")
    public BaseResponse<Long> createApp(@RequestBody AppAddRequest appAddRequest, HttpServletRequest request) {
        if (appAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long result = appService.createApp(appAddRequest, request);
        return ResultUtils.success(result);
    }

    /**
     * 用户根据 id 修改自己的应用
     *
     * @param appUpdateRequest 应用更新请求
     * @param request          请求对象
     * @return 更新是否成功
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateApp(@RequestBody AppUpdateRequest appUpdateRequest, HttpServletRequest request) {
        if (appUpdateRequest == null || appUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = appService.updateApp(appUpdateRequest, request);
        return ResultUtils.success(result);
    }

    /**
     * 用户根据 id 删除自己的应用
     *
     * @param deleteRequest 删除请求
     * @param request       请求对象
     * @return 删除是否成功
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteApp(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest.getId() == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = appService.deleteApp(deleteRequest.getId(), request);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 查看应用详情
     *
     * @param id 应用id
     * @return 应用详情
     */
    @GetMapping("/get/vo")
    public BaseResponse<AppVo> getAppById(long id) {
        AppVo appVo = appService.getAppVoById(id);
        return ResultUtils.success(appVo);
    }

    /**
     * 用户分页查询自己的应用列表
     *
     * @param appQueryRequest 查询请求
     * @param request         请求对象
     * @return 应用列表
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<AppVo>> getMyAppList(@RequestBody AppQueryRequest appQueryRequest, HttpServletRequest request) {
        if (appQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 获取当前登录用户
        User currentUser = userService.getCurrentLoginUser(request);

        // 限制每页最多20个
        if (appQueryRequest.getPageSize() > 20) {
            appQueryRequest.setPageSize(20);
        }

        Page<App> appPage = appService.page(
                Page.of(appQueryRequest.getPageNum(), appQueryRequest.getPageSize()),
                appService.getUserAppQueryWrapper(appQueryRequest, currentUser.getId()));

        Page<AppVo> appVoPage = new Page<>(appPage.getPageNumber(), appPage.getPageSize(), appPage.getTotalRow());
        List<App> appList = appPage.getRecords();
        List<AppVo> appVoList = appService.getAppVoList(appList);
        appVoPage.setRecords(appVoList);
        return ResultUtils.success(appVoPage);
    }


    /**
     * 分页获取精选应用列表
     *
     * @param appQueryRequest 查询请求
     * @return 精选应用列表
     */
    @PostMapping("/good/list/page/vo")
    public BaseResponse<Page<AppVo>> listGoodAppVOByPage(@RequestBody AppQueryRequest appQueryRequest) {
        ThrowUtils.throwIf(appQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 限制每页最多 20 个
        long pageSize = appQueryRequest.getPageSize();
        ThrowUtils.throwIf(pageSize > 20, ErrorCode.PARAMS_ERROR, "每页最多查询 20 个应用");
        long pageNum = appQueryRequest.getPageNum();
        // 只查询精选的应用
        appQueryRequest.setPriority(AppConstant.GOOD_APP_PRIORITY);
        QueryWrapper queryWrapper = appService.getQueryWrapper(appQueryRequest);
        // 分页查询
        Page<App> appPage = appService.page(Page.of(pageNum, pageSize), queryWrapper);
        // 数据封装
        Page<AppVo> appVOPage = new Page<>(pageNum, pageSize, appPage.getTotalRow());
        List<AppVo> appVOList = appService.getAppVoList(appPage.getRecords());
        appVOPage.setRecords(appVOList);
        return ResultUtils.success(appVOPage);
    }


    /**
     * 管理员根据 id 删除任意应用
     *
     * @param deleteRequest 删除请求
     * @return 删除是否成功
     */
    @PostMapping("/admin/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> adminDeleteApp(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest.getId() == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        App app = appService.getById(deleteRequest.getId());
        if (app == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        boolean result = appService.removeById(deleteRequest.getId());
        return ResultUtils.success(result);
    }

    /**
     * 管理员根据 id 更新任意应用
     *
     * @param appAdminUpdateRequest 应用更新请求
     * @return 更新是否成功
     */
    @PostMapping("/admin/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> adminUpdateApp(@RequestBody AppAdminUpdateRequest appAdminUpdateRequest) {
        if (appAdminUpdateRequest == null || appAdminUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        App oldApp = appService.getById(appAdminUpdateRequest.getId());
        if (oldApp == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        App app = new App();
        BeanUtil.copyProperties(appAdminUpdateRequest, app);
        boolean result = appService.updateById(app);
        return ResultUtils.success(result);
    }

    /**
     * 管理员分页查询应用列表
     *
     * @param appQueryRequest 查询请求
     * @return 应用列表
     */
    @PostMapping("/admin/list/page/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<AppVo>> adminGetAppList(@RequestBody AppQueryRequest appQueryRequest) {
        if (appQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Page<App> appPage = appService.page(
                Page.of(appQueryRequest.getPageNum(), appQueryRequest.getPageSize()),
                appService.getQueryWrapper(appQueryRequest));

        Page<AppVo> appVoPage = new Page<>(appPage.getPageNumber(), appPage.getPageSize(), appPage.getTotalRow());
        List<App> appList = appPage.getRecords();
        List<AppVo> appVoList = appService.getAppVoList(appList);
        appVoPage.setRecords(appVoList);
        return ResultUtils.success(appVoPage);
    }

    /**
     * 管理员根据 id 查看应用详情
     *
     * @param id 应用id
     * @return 应用详情
     */
    @GetMapping("/admin/get/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<AppVo> adminGetAppById(long id) {
        App app = appService.getById(id);
        return ResultUtils.success(appService.getAppVo(app));
    }

    /**
     * 聊天生成代码
     *
     * @param message
     * @param appId
     * @param request
     * @return
     */
    @GetMapping(value = "/chat/gen/code", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> chatToCode(@RequestParam String message, @RequestParam long appId, HttpServletRequest request) {
        // 1. 校验参数
        ThrowUtils.throwIf(appId <= 0, ErrorCode.PARAMS_ERROR, "appId不存在");
        ThrowUtils.throwIf(StrUtil.isBlank(message), ErrorCode.PARAMS_ERROR, "message不能为空");
        // 2. 获取登录用户
        User currentLoginUser = userService.getCurrentLoginUser(request);
        // 3. 流式输出
        Flux<String> chatToCodeStream = appService.chatToCode(appId, message, currentLoginUser);
        Flux<ServerSentEvent<String>> serverSentEventFlux = chatToCodeStream
                .map(chunk -> {
                    Map<String, Object> data = Map.of("d", chunk);
                    String jsonStr = JSONUtil.toJsonStr(data);
                    return ServerSentEvent
                            .<String>builder()
                            .data(jsonStr)
                            .build();
                })
                .concatWith(
                        Mono.just(ServerSentEvent.<String>builder()
                                .event("done")
                                .data("")
                                .build()));
        return serverSentEventFlux;
    }


    @GetMapping(value = "/chat/gen/code2", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatToCode2(@RequestParam String message, @RequestParam long appId, HttpServletRequest request) {
        // 1. 校验参数
        ThrowUtils.throwIf(appId <= 0, ErrorCode.PARAMS_ERROR, "appId不存在");
        ThrowUtils.throwIf(StrUtil.isBlank(message), ErrorCode.PARAMS_ERROR, "message不能为空");
        // 2. 获取登录用户
        User currentLoginUser = userService.getCurrentLoginUser(request);
        // 3. 流式输出
        Flux<String> chatToCodeStream = appService.chatToCode(appId, message, currentLoginUser);

        return chatToCodeStream;
    }
}
