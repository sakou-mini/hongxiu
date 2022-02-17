package com.donglai.web.web.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiParam;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("修改直播域名请求")
public class UpdateLiveDomainRequest {
    @ApiParam(value = "线路id", example = "0")
    private int lineCode;
    @ApiParam(value = "域名列表", example = "0")
    List<String> domainList;
}
