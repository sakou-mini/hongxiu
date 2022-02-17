package com.donglai.web.web.dto.request;

import lombok.Data;

/**
 * @author Moon
 * @date 2021-12-29 14:01
 */
@Data
public class AddBackUserRequest {

    private String nickname;

    private String roleId;

    private Boolean enabled;
}
