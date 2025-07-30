package com.flycode.flygenius.controller;

import com.flycode.flygenius.common.BaseResponse;
import com.flycode.flygenius.common.ResultUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author flycode
 */
@RequestMapping("/health")
@RestController
@Tag(name = "测试接口")
public class HealthController {

    @GetMapping("/test")
    @Operation(summary = "测试接口是否正常响应数据")
    public BaseResponse<String> testOk() {
        return ResultUtils.success("ok");
    }
}
