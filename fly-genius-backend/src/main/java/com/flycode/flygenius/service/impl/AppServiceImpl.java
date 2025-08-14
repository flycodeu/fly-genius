package com.flycode.flygenius.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.flycode.flygenius.ai.model.CodeGenTypeEnum;
import com.flycode.flygenius.core.AiCodeGeneratorFacade;
import com.flycode.flygenius.entity.constants.AppConstant;
import com.flycode.flygenius.entity.constants.UserConstant;
import com.flycode.flygenius.entity.enums.ChatHistoryMessageTypeEnum;
import com.flycode.flygenius.entity.model.App;
import com.flycode.flygenius.entity.model.User;
import com.flycode.flygenius.entity.request.app.AppAddRequest;
import com.flycode.flygenius.entity.request.app.AppQueryRequest;
import com.flycode.flygenius.entity.request.app.AppUpdateRequest;
import com.flycode.flygenius.entity.vo.AppVo;
import com.flycode.flygenius.entity.vo.UserVo;
import com.flycode.flygenius.exception.BusinessException;
import com.flycode.flygenius.exception.ErrorCode;
import com.flycode.flygenius.exception.ThrowUtils;
import com.flycode.flygenius.mapper.AppMapper;
import com.flycode.flygenius.service.AppService;
import com.flycode.flygenius.service.UserService;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;
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
    @Autowired
    private AiCodeGeneratorFacade aiCodeGeneratorFacade;
    @Autowired
    private ChatHistoryServiceImpl chatHistoryService;

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

    @Override
    public Flux<String> chatToCode(long appId, String message, User loginUser) {
        // 1. 参数校验
        ThrowUtils.throwIf(appId <= 0, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(StrUtil.isBlank(message), ErrorCode.PARAMS_ERROR);
        // 2.应用是否存在
        App app = this.getById(appId);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR, "找不到对应的应用");
        // 3. 校验权限
        Long userId = loginUser.getId();
        if (!userId.equals(app.getUserId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无访问权限");
        }
        // 4.获取类型
        String codeGenType = app.getCodeGenType();
        CodeGenTypeEnum codeGenTypeEnum = CodeGenTypeEnum.getEnumByValue(codeGenType);
        if (codeGenTypeEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不存在对应类型");
        }

        // 添加用户消息到历史数据库
        chatHistoryService.addChatHistory(appId, message, userId, ChatHistoryMessageTypeEnum.USER.getValue());

        // 5.生成代码
        Flux<String> saveFileStream = aiCodeGeneratorFacade.generatorAndSaveFileStream(message, codeGenTypeEnum, appId);


        // 6. 插入历史数据库
        StringBuilder aiCode = new StringBuilder();
        saveFileStream.map(chunk -> {
            aiCode.append(chunk);
            return chunk;
        }).doOnComplete(() -> {
            String aiResponse = aiCode.toString();
            chatHistoryService.addChatHistory(appId, aiResponse, userId, ChatHistoryMessageTypeEnum.AI.getValue());
        }).doOnError((error) -> {
            String errorMessage = "AI回复消息失败：" + error.getMessage();
            chatHistoryService.addChatHistory(appId, errorMessage, userId, ChatHistoryMessageTypeEnum.AI.getValue());
        });


        return saveFileStream;
    }

    @Override
    public String deployApp(Long appId, User loginUser) {
        // 1. 参数校验
        ThrowUtils.throwIf(appId <= 0, ErrorCode.PARAMS_ERROR, "appId不存在");
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR, "用户未登录");
        // 2. 应用是否存在
        App app = this.getById(appId);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR, "应用不存在");
        // 3. 用户校验是否是本人
        Long userId = app.getUserId();
        ThrowUtils.throwIf(!userId.equals(loginUser.getId()), ErrorCode.NO_AUTH_ERROR, "无访问权限");

        // 4. 判断是否有deployKey
        String deployKey = app.getDeployKey();
        if (deployKey == null) {
            // 5. 没有就生成6位随机的deployKey
            deployKey = RandomUtil.randomString(6);
        }

        // 6. 获取代码类型，拼接文件生成路径
        String codeGenType = app.getCodeGenType();
        String sourceDirName = codeGenType + "_" + appId;
        String sourceDirPath = AppConstant.CODE_OUTPUT_ROOT_DIR + File.separator + sourceDirName;

        // 7. 判断路径是否存在、文件是否存在
        File sourceDir = new File(sourceDirPath);
        if (!sourceDir.exists() || !sourceDir.isDirectory()) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "代码生成目录不存在");
        }

        // 8. 将生成的代码复制到部署目录路径下面
        String deployDirPath = AppConstant.CODE_DEPLOY_ROOT_DIR + File.separator + deployKey;
        try {
            FileUtil.copyContent(sourceDir, new File(deployDirPath), true);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "代码生成目录不存在");
        }
        // 9. 修改部署时间
        app.setDeployedTime(LocalDateTime.now());
        app.setDeployKey(deployKey);
        app.setId(appId);
        boolean b = this.updateById(app);
        ThrowUtils.throwIf(!b, ErrorCode.SYSTEM_ERROR, "修改应用部署信息失败");

        // 10. 返回访问地址
        return String.format("%s/%s/", AppConstant.CODE_DEPLOY_HOST, deployKey);
    }

}
