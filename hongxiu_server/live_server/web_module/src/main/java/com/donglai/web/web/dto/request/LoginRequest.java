package com.donglai.web.web.dto.request;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value = "音乐列表")
public class LoginRequest {
    private String username;
    private String password;
}