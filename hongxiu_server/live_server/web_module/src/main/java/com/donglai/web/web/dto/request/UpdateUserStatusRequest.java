package com.donglai.web.web.dto.request;

import lombok.Data;

import java.util.List;

/**
 * @author Moon
 * @date 2021-12-21 15:39
 */
@Data
public class UpdateUserStatusRequest {

    private List<String> userIds;

    private Boolean status;

    private String reason;
}
