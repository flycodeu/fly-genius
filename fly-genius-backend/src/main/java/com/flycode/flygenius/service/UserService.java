package com.flycode.flygenius.service;

import com.flycode.flygenius.entity.request.user.UserLoginRequest;
import com.flycode.flygenius.entity.request.user.UserQueryRequest;
import com.flycode.flygenius.entity.request.user.UserRegisterRequest;
import com.flycode.flygenius.entity.vo.LoginUserVo;
import com.flycode.flygenius.entity.vo.UserVo;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.flycode.flygenius.entity.model.User;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * 用户 服务层。
 *
 * @author flycode
 */
public interface UserService extends IService<User> {
    /**
     * 用户注册
     *
     * @param userRegisterRequest 用户注册请求
     * @return 注册成功用户id
     */
    long userRegister(UserRegisterRequest userRegisterRequest);

    /**
     * 将user封装
     *
     * @param user 用户信息
     * @return 封装后的登录用户信息
     */
    LoginUserVo getLoginUserVo(User user);

    /**
     * 用户登录
     *
     * @param userLoginRequest 用户登录请求
     * @param request
     * @return 登录用户信息
     */
    LoginUserVo userLogin(UserLoginRequest userLoginRequest, HttpServletRequest request);


    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    User getCurrentLoginUser(HttpServletRequest request);


    /**
     * 用户退出
     *
     * @param request
     * @return
     */
    Boolean logout(HttpServletRequest request);

    /**
     * 获取封装的用户信息
     * @param user
     * @return
     */
    UserVo getUserVo(User user);

    /**
     * 获取用户列表
     * @param userList
     * @return
     */
    List<UserVo> getUserVoList(List<User> userList);

    QueryWrapper getQueryWrapper(UserQueryRequest queryRequest);

    String entryPassword(String userPassword);
}
