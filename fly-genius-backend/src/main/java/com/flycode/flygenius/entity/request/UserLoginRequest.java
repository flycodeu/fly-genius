package com.flycode.flygenius.entity.request;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * 用户登录请求
 *
 * @author flycode
 */
@Data
public class UserLoginRequest {

    @NotBlank(message = "账号名不能为空")
    @Length(min = 4, max = 10, message = "账号长度4-10位")
    private String userAccount;

    @NotBlank(message = "密码不能为空")
    @Length(min = 6, max = 10, message = "密码长度6-10位")
    private String userPassword;

}
