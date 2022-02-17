package com.donglai.web.web.dto.request;

import lombok.Data;

/**
 * @author Moon
 * @date 2021-12-21 16:08
 */
@Data
public class AddUserRequest {

    private String nickname;

    private String phoneNumber;

    private String password;

    private Boolean status;
}
