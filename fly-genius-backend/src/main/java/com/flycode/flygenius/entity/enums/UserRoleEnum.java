package com.flycode.flygenius.entity.enums;

import lombok.Data;
import lombok.Getter;

/**
 * 用户角色枚举
 *
 * @author flycode
 */
@Getter
public enum UserRoleEnum {
    USER("用户", "user"),
    ADMIN("管理员", "admin");;

    private String text;

    private String value;

    UserRoleEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }


    public static UserRoleEnum getUserRoleByValue(String value) {
        if (value == null) {
            return null;
        }
        for (UserRoleEnum userRoleEnum : UserRoleEnum.values()) {
            if (userRoleEnum.value.equals(value)) {
                return userRoleEnum;
            }
        }
        return null;
    }
}
