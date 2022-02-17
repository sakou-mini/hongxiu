package com.donglai.web.web.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiParam;
import lombok.Data;

@Data
@ApiModel(value =  "设置域名")
public class H5DomainSetRequest {
    @ApiParam(value = "域名id")
    public long id;
    @ApiParam(value = "新域名称")
    public String domain;
}
