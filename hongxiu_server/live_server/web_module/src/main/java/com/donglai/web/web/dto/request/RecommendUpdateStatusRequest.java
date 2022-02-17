package com.donglai.web.web.dto.request;

import lombok.Data;

import java.util.List;

/**
 * @author Moon
 * @date 2022-01-05 16:19
 */
@Data
public class RecommendUpdateStatusRequest {

    private List<Long> ids;

    private Boolean status;
}
