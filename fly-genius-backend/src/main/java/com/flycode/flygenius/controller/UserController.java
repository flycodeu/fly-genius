package com.flycode.flygenius.controller;

import cn.hutool.core.bean.BeanUtil;
import com.flycode.flygenius.annotation.AuthCheck;
import com.flycode.flygenius.common.BaseResponse;
import com.flycode.flygenius.common.DeleteRequest;
import com.flycode.flygenius.common.ResultUtils;
import com.flycode.flygenius.entity.constants.UserConstant;
import com.flycode.flygenius.entity.request.user.*;
import com.flycode.flygenius.entity.vo.LoginUserVo;
import com.flycode.flygenius.entity.vo.UserVo;
import com.flycode.flygenius.exception.BusinessException;
import com.flycode.flygenius.exception.ErrorCode;
import com.flycode.flygenius.exception.ThrowUtils;
import com.mybatisflex.core.paginate.Page;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import com.flycode.flygenius.entity.model.User;
import com.flycode.flygenius.service.UserService;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户 控制层。
 *
 * @author flycode
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 管理员更新用户
     *
     * @param userUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
        if (userUpdateRequest == null || userUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtil.copyProperties(userUpdateRequest, user);
        boolean update = userService.updateById(user);
        ThrowUtils.throwIf(!update, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }


    /**
     * 管理员创建用户
     *
     * @param userAddRequest
     * @return
     */
    @PostMapping("/save")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> save(@RequestBody UserAddRequest userAddRequest) {
        if (userAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtil.copyProperties(userAddRequest, user);
        String entryPassword = userService.entryPassword("123456");
        user.setUserPassword(entryPassword);
        boolean save = userService.save(user);
        ThrowUtils.throwIf(!save, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(user.getId());
    }


    @PostMapping("/page")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Page<UserVo>> getUserVoPage(@RequestBody UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Page<User> userPage = userService
                .page(
                        Page.of(userQueryRequest.getPageNum(), userQueryRequest.getPageSize()),
                        userService.getQueryWrapper(userQueryRequest));

        Page<UserVo> userVoPage = new Page<>(userPage.getPageNumber(), userPage.getPageSize(), userPage.getTotalRow());
        List<User> userList = userPage.getRecords();
        List<UserVo> userVoList = userService.getUserVoList(userList);
        userVoPage.setRecords(userVoList);
        return ResultUtils.success(userVoPage);
    }


    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest.getId() == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean delete = userService.removeById(deleteRequest.getId());
        return ResultUtils.success(delete);
    }


    @GetMapping("/userLogout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        boolean logout = userService.logout(request);
        return ResultUtils.success(logout);
    }

    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        long result = userService.userRegister(userRegisterRequest);
        return ResultUtils.success(result);
    }


    @PostMapping("/login")
    public BaseResponse<LoginUserVo> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        LoginUserVo loginUserVo = userService.userLogin(userLoginRequest, request);
        return ResultUtils.success(loginUserVo);
    }


    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<User> getUserById(long id) {
        User user = userService.getById(id);
        return ResultUtils.success(user);
    }


    @GetMapping("/get/vo")
    public BaseResponse<UserVo> getUserVoById(long id) {
        User user = userService.getById(id);
        UserVo userVo = userService.getUserVo(user);
        return ResultUtils.success(userVo);
    }

    @GetMapping("/getLoginUser")
    public BaseResponse<LoginUserVo> getLoginUser(HttpServletRequest request) {
        LoginUserVo loginUserVo = userService.getLoginUserVo(userService.getCurrentLoginUser(request));
        return ResultUtils.success(loginUserVo);
    }

}
