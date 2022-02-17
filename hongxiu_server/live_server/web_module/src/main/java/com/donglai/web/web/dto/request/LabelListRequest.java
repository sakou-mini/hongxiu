package com.donglai.web.web.dto.request;

import lombok.Data;

/**
 * @author Moon
 * @date 2021-12-24 11:11
 */

@Data
public class LabelListRequest {

    private Integer labelId;

    private String labelName;

    private Boolean enable;

    private Integer page;

    private Integer size;

}
