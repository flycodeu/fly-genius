package com.flycode.flygenius.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.flycode.flygenius.entity.enums.UserRoleEnum;
import com.flycode.flygenius.entity.model.User;
import com.flycode.flygenius.entity.request.user.UserLoginRequest;
import com.flycode.flygenius.entity.request.user.UserQueryRequest;
import com.flycode.flygenius.entity.request.user.UserRegisterRequest;
import com.flycode.flygenius.entity.vo.LoginUserVo;
import com.flycode.flygenius.entity.vo.UserVo;
import com.flycode.flygenius.exception.BusinessException;
import com.flycode.flygenius.exception.ErrorCode;
import com.flycode.flygenius.mapper.UserMapper;
import com.flycode.flygenius.service.UserService;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.flycode.flygenius.entity.constants.UserConstant.USER_LOGIN_STATE;

/**
 * 用户 服务层实现。
 *
 * @author flycode
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public long userRegister(UserRegisterRequest userRegisterRequest) {
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();

        // 1.校验密码
        if (!checkPassword.equals(userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }

        // 2. 判断账号是否存在
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("userAccount", userAccount);
        boolean exists = this.exists(queryWrapper);
        if (exists) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号已存在");
        }

        // 3. 插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(entryPassword(userPassword));
        user.setUserName("无名");
        user.setUserRole(UserRoleEnum.USER.getValue());
        boolean save = this.save(user);
        if (!save) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败");
        }

        return user.getId();
    }

    @Override
    public LoginUserVo getLoginUserVo(User user) {
        if (user != null) {
            LoginUserVo loginUserVo = new LoginUserVo();
            BeanUtils.copyProperties(user, loginUserVo);
            return loginUserVo;
        }
        return null;
    }

    @Override
    public LoginUserVo userLogin(UserLoginRequest userLoginRequest, HttpServletRequest request) {
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        // 1. 密码加密
        String entryPassword = entryPassword(userPassword);
        // 2. 判断账号是否存在
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", entryPassword);
        User user = this.getOne(queryWrapper);
        if (ObjectUtil.isEmpty(user)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }

        // 3. 用户存在，返回脱敏信息
        LoginUserVo loginUserVo = this.getLoginUserVo(user);
        // 4. 设置到session
        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        return loginUserVo;
    }

    @Override
    public User getCurrentLoginUser(HttpServletRequest request) {
        // 1. 判断是否登录
        Object attribute = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) attribute;
        if (user == null || user.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "用户未登录");
        }

        // 2. 判断用户登录账号是否和数据库账号一致
        Long userId = user.getId();
        User userDb = this.getById(userId);
        if (userDb == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "用户未登录");
        }
        return userDb;
    }

    @Override
    public Boolean logout(HttpServletRequest request) {
        // 1. 判断用户是否登录
        Object attribute = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) attribute;
        if (user == null || user.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "用户未登录");
        }

        // 2. 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }

    @Override
    public UserVo getUserVo(User user) {
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(user, userVo);
        return userVo;
    }

    @Override
    public List<UserVo> getUserVoList(List<User> userList) {
        if (CollUtil.isEmpty(userList)) {
            return CollUtil.newArrayList();
        }
        return userList.stream().map(this::getUserVo).toList();
    }

    @Override
    public QueryWrapper getQueryWrapper(UserQueryRequest queryRequest) {
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = queryRequest.getId();
        String userName = queryRequest.getUserName();
        String userAccount = queryRequest.getUserAccount();
        String userProfile = queryRequest.getUserProfile();
        String userRole = queryRequest.getUserRole();
        int pageNum = queryRequest.getPageNum();
        int pageSize = queryRequest.getPageSize();
        String sortField = queryRequest.getSortField();
        String sortOrder = queryRequest.getSortOrder();

        return QueryWrapper.create()
                .like(User::getUserAccount, userAccount)
                .like(User::getUserProfile, userProfile)
                .like(User::getUserName, userName)
                .eq(User::getUserRole, userRole)
                .eq(User::getId, id)
                .orderBy(sortField,"ascend".equals(sortOrder));
    }

    /**
     * 校验密码
     *
     * @param userPassword 密码
     * @return
     */
    @Override
    public String entryPassword(String userPassword) {
        String salt = "flycode";
        return DigestUtils.md5DigestAsHex((salt + userPassword).getBytes(StandardCharsets.UTF_8));
    }
}
