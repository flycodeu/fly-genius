package com.flycode.flygenius.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.flycode.flygenius.ai.model.CodeGenTypeEnum;
import com.flycode.flygenius.entity.constants.AppConstant;
import com.flycode.flygenius.entity.constants.UserConstant;
import com.flycode.flygenius.entity.model.App;
import com.flycode.flygenius.entity.model.User;
import com.flycode.flygenius.entity.request.app.AppAddRequest;
import com.flycode.flygenius.entity.request.app.AppQueryRequest;
import com.flycode.flygenius.entity.request.app.AppUpdateRequest;
import com.flycode.flygenius.entity.vo.AppVo;
import com.flycode.flygenius.entity.vo.UserVo;
import com.flycode.flygenius.exception.BusinessException;
import com.flycode.flygenius.exception.ErrorCode;
import com.flycode.flygenius.mapper.AppMapper;
import com.flycode.flygenius.service.AppService;
import com.flycode.flygenius.service.UserService;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 应用 服务层实现。
 *
 * @author flycode
 */
@Service
public class AppServiceImpl extends ServiceImpl<AppMapper, App> implements AppService {

    @Autowired
    private UserService userService;

    @Override
    public long createApp(AppAddRequest appAddRequest, HttpServletRequest request) {
        if (appAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 获取当前登录用户
        User currentUser = userService.getCurrentLoginUser(request);

        // 校验参数
        String initPrompt = appAddRequest.getInitPrompt();
        if (ObjectUtil.isEmpty(initPrompt)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "初始化提示词不能为空");
        }

        // 创建应用
        App app = new App();
        BeanUtils.copyProperties(appAddRequest, app);
        app.setUserId(currentUser.getId());
        app.setPriority(AppConstant.DEFAULT_APP_PRIORITY);
        // 默认使用多文件代码生成
        app.setCodeGenType(CodeGenTypeEnum.MULTI_FILE.getValue());

        boolean save = this.save(app);
        if (!save) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建应用失败");
        }

        return app.getId();
    }

    @Override
    public boolean updateApp(AppUpdateRequest appUpdateRequest, HttpServletRequest request) {
        if (appUpdateRequest == null || appUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 获取当前登录用户
        User currentUser = userService.getCurrentLoginUser(request);
        Long appId = appUpdateRequest.getId();

        // 获取应用信息
        App app = this.getById(appId);
        if (app == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "应用不存在");
        }

        // 判断权限：只有管理员或应用创建者可以修改
        if (!UserConstant.ADMIN_ROLE.equals(currentUser.getUserRole()) && !app.getUserId().equals(currentUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限修改此应用");
        }

        // 更新应用信息
        App updateApp = new App();
        BeanUtils.copyProperties(appUpdateRequest, updateApp);
        updateApp.setUpdateTime(LocalDateTime.now());
        updateApp.setEditTime(LocalDateTime.now());

        return this.updateById(updateApp);
    }

    @Override
    public boolean deleteApp(Long id, HttpServletRequest request) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 获取当前登录用户
        User currentUser = userService.getCurrentLoginUser(request);

        // 获取应用信息
        App app = this.getById(id);
        if (app == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "应用不存在");
        }

        // 判断权限：只有管理员或应用创建者可以删除
        if (!UserConstant.ADMIN_ROLE.equals(currentUser.getUserRole()) && !app.getUserId().equals(currentUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限删除此应用");
        }

        return this.removeById(id);
    }

    @Override
    public AppVo getAppVoById(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        App app = this.getById(id);
        return getAppVo(app);
    }

    @Override
    public AppVo getAppVo(App app) {
        if (app == null) {
            return null;
        }
        AppVo appVo = new AppVo();
        BeanUtils.copyProperties(app, appVo);
        // 关联用户信息
        if (appVo.getUserId() != null) {
            User user = userService.getById(appVo.getUserId());
            UserVo userVo = userService.getUserVo(user);
            appVo.setUser(userVo);
        }
        return appVo;
    }

    @Override
    public List<AppVo> getAppVoList(List<App> appList) {
        if (CollUtil.isEmpty(appList)) {
            return new ArrayList<>();
        }
        // 批量获取用户信息，避免 N+1 查询问题
        Set<Long> userIds = appList.stream()
                .map(App::getUserId)
                .collect(Collectors.toSet());
        Map<Long, UserVo> userVOMap = userService.listByIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, userService::getUserVo));
        return appList.stream().map(app -> {
            AppVo appVO = getAppVo(app);
            UserVo userVO = userVOMap.get(app.getUserId());
            appVO.setUser(userVO);
            return appVO;
        }).collect(Collectors.toList());
    }


    @Override
    public QueryWrapper getQueryWrapper(AppQueryRequest appQueryRequest) {
        if (appQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = appQueryRequest.getId();
        String appName = appQueryRequest.getAppName();
        String cover = appQueryRequest.getCover();
        String initPrompt = appQueryRequest.getInitPrompt();
        String codeGenType = appQueryRequest.getCodeGenType();
        String deployKey = appQueryRequest.getDeployKey();
        Integer priority = appQueryRequest.getPriority();
        Long userId = appQueryRequest.getUserId();
        String sortField = appQueryRequest.getSortField();
        String sortOrder = appQueryRequest.getSortOrder();
        return QueryWrapper.create()
                .eq("id", id)
                .like("appName", appName)
                .like("cover", cover)
                .like("initPrompt", initPrompt)
                .eq("codeGenType", codeGenType)
                .eq("deployKey", deployKey)
                .eq("priority", priority)
                .eq("userId", userId)
                .orderBy(sortField, "ascend".equals(sortOrder));
    }


    @Override
    public QueryWrapper getUserAppQueryWrapper(AppQueryRequest queryRequest, Long userId) {
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String appName = queryRequest.getAppName();

        return QueryWrapper.create()
                .eq(App::getUserId, userId)
                .like(App::getAppName, appName)
                .orderBy(App::getCreateTime, false);
    }

}
