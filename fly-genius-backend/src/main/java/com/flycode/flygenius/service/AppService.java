package com.flycode.flygenius.service;

import com.flycode.flygenius.entity.model.User;
import com.flycode.flygenius.entity.request.app.AppAddRequest;
import com.flycode.flygenius.entity.request.app.AppQueryRequest;
import com.flycode.flygenius.entity.request.app.AppUpdateRequest;
import com.flycode.flygenius.entity.vo.AppVo;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.flycode.flygenius.entity.model.App;
import jakarta.servlet.http.HttpServletRequest;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * 应用 服务层。
 *
 * @author flycode
 */
public interface AppService extends IService<App> {
    /**
     * 创建应用
     *
     * @param appAddRequest 应用创建请求
     * @param request       请求对象
     * @return 创建成功应用id
     */
    long createApp(AppAddRequest appAddRequest, HttpServletRequest request);

    /**
     * 更新应用
     *
     * @param appUpdateRequest 应用更新请求
     * @param request          请求对象
     * @return 更新是否成功
     */
    boolean updateApp(AppUpdateRequest appUpdateRequest, HttpServletRequest request);

    /**
     * 删除应用
     *
     * @param id      应用id
     * @param request 请求对象
     * @return 删除是否成功
     */
    boolean deleteApp(Long id, HttpServletRequest request);

    /**
     * 获取应用详情
     *
     * @param id 应用id
     * @return 应用详情
     */
    AppVo getAppVoById(Long id);

    /**
     * 获取封装的应用信息
     *
     * @param app 应用信息
     * @return 封装后的应用信息
     */
    AppVo getAppVo(App app);

    /**
     * 获取应用列表
     *
     * @param appList 应用列表
     * @return 封装后的应用列表
     */
    List<AppVo> getAppVoList(List<App> appList);

    /**
     * 获取查询包装器
     *
     * @param queryRequest 查询请求
     * @return 查询包装器
     */
    QueryWrapper getQueryWrapper(AppQueryRequest queryRequest);

    /**
     * 获取用户自己的应用查询包装器
     *
     * @param queryRequest 查询请求
     * @param userId       用户id
     * @return 查询包装器
     */
    QueryWrapper getUserAppQueryWrapper(AppQueryRequest queryRequest, Long userId);


    /**
     * AI聊天生成代码
     *
     * @param appId
     * @param message
     * @param loginUser
     * @return
     */
    Flux<String> chatToCode(long appId, String message, User loginUser);

    /**
     * 应用部署
     *
     * @param appId 应用id
     * @param loginUser 登录用户
     * @return
     */
    String deployApp(Long appId, User loginUser);
}
