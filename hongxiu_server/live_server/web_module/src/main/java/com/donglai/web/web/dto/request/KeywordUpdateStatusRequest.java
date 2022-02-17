package com.donglai.web.web.dto.request;

import lombok.Data;

import java.util.List;

/**
 * @author Moon
 * @date 2021-12-28 15:13
 */
@Data
public class KeywordUpdateStatusRequest {

    private List<Long> ids;

    private Boolean status;
}
