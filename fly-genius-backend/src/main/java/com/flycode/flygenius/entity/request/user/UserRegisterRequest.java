package com.flycode.flygenius.entity.request.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * 用户注册请求体
 * @author flycode
 */
@Data
public class UserRegisterRequest {

    @NotBlank(message = "账号名不能为空")
    @Length(min = 4, max = 10, message = "账号长度4-10位")
    private String userAccount;

    @NotBlank(message = "密码不能为空")
    @Length(min = 6, max = 10, message = "密码长度6-10位")
    private String userPassword;

    @NotBlank(message = "确认密码不能为空")
    @Length(min = 6, max = 10, message = "密码长度6-10位")
    private String checkPassword;
}
