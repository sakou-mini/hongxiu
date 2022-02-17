package com.donglai.web.web.controller.server;

import com.donglai.web.response.RestResponse;
import com.donglai.web.response.SuccessResponse;
import com.donglai.web.web.dto.request.LabelListRequest;
import com.donglai.web.web.dto.request.UpdateLabelListStatusRequest;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Moon
 * @date 2021-12-24 11:08
 */
@Api(tags = {"标签管理"})
@RestController
@RequestMapping("/api/v1/label")
public class LabelController {

    @Autowired
    com.donglai.web.db.server.service.LabelDbService labelDbService;

    @PostMapping("/findLabelList")
    public RestResponse findLabelList(LabelListRequest request) {
        return new SuccessResponse().withData(labelDbService.conditionGetLabel(request));
    }

    @PostMapping("/addLabel")
    public RestResponse addLabel(String labelName) {
        return labelDbService.addLabel(labelName);
    }

    @PostMapping("/updateStatusList")
    public RestResponse updateStatusList(UpdateLabelListStatusRequest request) {
        return new SuccessResponse().withData(labelDbService.updateStatusList(request));
    }

    @PostMapping("/delLabelList")
    public RestResponse delLabelList(UpdateLabelListStatusRequest request) {
        return new SuccessResponse().withData(labelDbService.delLabelList(request));
    }

    @PostMapping("/update")
    public RestResponse update(LabelListRequest labelListRequest) {
        return labelDbService.update(labelListRequest);
    }


    @PostMapping("/findAll")
    public RestResponse findAll() {
        return labelDbService.findAll();
    }
}
