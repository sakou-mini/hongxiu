package com.donglai.web.web.controller.server;

import com.donglai.web.response.RestResponse;
import com.donglai.web.response.SuccessResponse;
import com.donglai.web.service.KeywordBackService;
import com.donglai.web.web.dto.request.KeywordFindListRequest;
import com.donglai.web.web.dto.request.KeywordInsertRequest;
import com.donglai.web.web.dto.request.KeywordUpdateStatusRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Moon
 * @date 2021-12-28 14:07
 */
@Api(tags = "敏感词管理")
@RestController
@RequestMapping("/api/v1/keyword")
public class KeywordController {

    @Autowired
    private KeywordBackService keywordService;

    @ApiOperation(value = "敏感词列表")
    @PostMapping("/findList")
    public RestResponse findList(KeywordFindListRequest request) {
        return new SuccessResponse().withData(keywordService.findList(request));
    }

    @ApiOperation(value = "修改敏感词状态")
    @PostMapping("/updateStatus")
    public RestResponse updateStatus(KeywordUpdateStatusRequest request) {
        return keywordService.updateStatus(request);
    }

    @ApiOperation(value = "添加敏感词")
    @PostMapping("/insert")
    public RestResponse insert(KeywordInsertRequest request) {
        return keywordService.insert(request);
    }

    @ApiOperation(value = "删除敏感词")
    @PostMapping("/delete")
    public RestResponse delete(KeywordUpdateStatusRequest request) {
        return keywordService.delete(request);
    }

}
