package com.flycode.flygenius.entity.vo;

import lombok.Data;

/**
 * 封装用户信息返回的数据
 *
 * @author flycode
 */
@Data
public class LoginUserVo {

    /**
     * id
     */
    private Long id;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户角色：user/admin
     */
    private String userRole;

}
