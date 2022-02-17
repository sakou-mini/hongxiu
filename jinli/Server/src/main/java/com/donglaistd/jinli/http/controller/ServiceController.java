package com.donglaistd.jinli.http.controller;

import com.donglaistd.jinli.http.entity.PageInfo;
import com.donglaistd.jinli.service.statistic.ServicePageProcess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
@RequestMapping("backOffice/")
public class ServiceController {
    @Autowired
    ServicePageProcess servicePageProcess;
    @Value("${environment.name}")
    public String environment;

    @RequestMapping("/guess/feedback")
    @ResponseBody
    public ModelAndView feedback(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("service/feedback.html");
    }

    @RequestMapping("/service/feedbackPageInfo")
    @ResponseBody
    public PageInfo<Map<String,Object>> feedbackPageInfo(Long startTime, Long endTime , String keyWords, int page, int size){
        return servicePageProcess.getFeedbackPageInfo(startTime,endTime,keyWords,page-1,size);
    }

    @GetMapping("/service/environment")
    @ResponseBody
    public String feedbackPageInfo(){
        return environment;
    }

}
