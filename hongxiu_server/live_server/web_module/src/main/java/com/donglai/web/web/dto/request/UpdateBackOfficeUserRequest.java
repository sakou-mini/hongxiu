package com.donglai.web.web.dto.request;

import lombok.Data;

/**
 * @author Moon
 * @date 2021-12-31 15:52
 */
@Data
public class UpdateBackOfficeUserRequest {
    private String id;

    private String username;

    private String phone;

    private String email;

    private String avatar;
}
