package com.donglai.web.web.controller.server;

import com.donglai.web.response.RestResponse;
import com.donglai.web.response.SuccessResponse;
import com.donglai.web.service.TouristService;
import com.donglai.web.web.dto.request.TouristListRequest;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Moon
 * @date 2021-12-22 10:30
 */
@RestController
@RequestMapping("/api/v1/tourist")
@Api(tags = "用户游客管理")
public class TouristController {

    @Autowired
    private TouristService touristService;


    @PostMapping("/touristList")
    public RestResponse touristList(TouristListRequest request) {
        return new SuccessResponse().withData(touristService.touristList(request));
    }

    @PostMapping("/touristStatisticsList")
    public RestResponse touristStatisticsList(TouristListRequest request) {
        return new SuccessResponse().withData(touristService.touristStatisticsList(request));
    }
}
