package com.donglai.web.web.dto.request;

import lombok.Data;

/**
 * @author Moon
 * @date 2021-12-28 17:46
 */
@Data
public class FeedbackUpdateStatusRequest {

    private Long id;

    private Integer status;

    private String reply;

}
