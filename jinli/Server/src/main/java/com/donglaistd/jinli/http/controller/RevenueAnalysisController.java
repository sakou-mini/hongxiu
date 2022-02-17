package com.donglaistd.jinli.http.controller;


import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.statistic.DailyBetInfo;
import com.donglaistd.jinli.http.entity.BetRecordSummary;
import com.donglaistd.jinli.http.entity.HttpURLConnection;
import com.donglaistd.jinli.http.entity.PageInfo;
import com.donglaistd.jinli.http.entity.statistic.UserGiftDetailData;
import com.donglaistd.jinli.service.statistic.RevenueAnalysisProcess;
import com.donglaistd.jinli.util.TimeUtil;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("backOffice/revenueAnalysis/")
public class RevenueAnalysisController {
    @Autowired
    private RevenueAnalysisProcess revenueAnalysisProcess;

    @RequestMapping("/newUserChart")
    @ResponseBody
    public Object newUserChart() {
        return new ModelAndView("User/newUserChart.html");
    }

    @RequestMapping("/activeUserChart")
    @ResponseBody
    public Object activeUserChart() {
        return new ModelAndView("User/activeUserChart.html");
    }

    @RequestMapping("/conversionRatesChart")
    @ResponseBody
    public Object conversionRatesChart() {
        return new ModelAndView("User/conversionRatesChart.html");
    }

    @RequestMapping("/retainedUserChart")
    @ResponseBody
    public Object retainedUserChart() {
        return new ModelAndView("User/retainedUserChart.html");
    }

    @RequestMapping("/mobileDevicesChart")
    @ResponseBody
    public Object mobileDevicesChart() {
        return new ModelAndView("User/mobileDevicesChart.html");
    }

    @RequestMapping("/revenueIndex")
    @ResponseBody
    public ModelAndView revenueIndex(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("revenueAnalysis/revenueIndex.html");
    }

    @RequestMapping("/betsRecordList")
    @ResponseBody
    public ModelAndView betsRecordList(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("revenueAnalysis/betsRecordList.html");
    }

    @RequestMapping("/revenueData")
    @ResponseBody
    public ModelAndView revenueData(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("revenueAnalysis/revenueData.html");
    }

    @RequestMapping("/paymentPenetration")
    @ResponseBody
    public ModelAndView paymentPenetration(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("revenueAnalysis/paymentPenetration.html");
    }

    @RequestMapping("/newPlayerValue")
    @ResponseBody
    public ModelAndView newPlayerValue(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("revenueAnalysis/newPlayerValue.html");
    }

    @RequestMapping("/paymentHabit")
    @ResponseBody
    public ModelAndView paymentHabit(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("revenueAnalysis/paymentHabit.html");
    }

    @RequestMapping("/getGiftRecords")
    @ResponseBody
    public HttpURLConnection<UserGiftDetailData> getGiftConsumptionRecords(long startTime, long endTime,String userId,String userName,String roomId,int page,int size, int platform) {
        var sendData = new HttpURLConnection<UserGiftDetailData>();
        Constant.PlatformType platformType = Constant.PlatformType.forNumber(platform);
        if(platformType == null) return sendData;
        PageInfo<UserGiftDetailData> pageInfo = revenueAnalysisProcess.findGiftSpendDetail(startTime, TimeUtil.getDayEndTime(endTime),userId, userName, roomId,(page-1), size,platformType);
        sendData.code = pageInfo.resultCode;
        sendData.data = Lists.newArrayList(pageInfo.getContent());
        sendData.count = (int) pageInfo.getTotal();
        return sendData;
    }

    @RequestMapping("/getGiftIncomeRecords")
    @ResponseBody
    public HttpURLConnection<UserGiftDetailData> getGiftIncomeRecords(long startTime, long endTime,String liveUserId,String liveUserName,String roomId,int page,int size, int platform) {
        var sendData = new HttpURLConnection<UserGiftDetailData>();
        Constant.PlatformType platformType = Constant.PlatformType.forNumber(platform);
        if(platformType == null) return sendData;
        PageInfo<UserGiftDetailData> pageInfo = revenueAnalysisProcess.findGiftIncomeRecords(PageRequest.of(page-1,size),startTime,
                TimeUtil.getDayEndTime(endTime),liveUserId, liveUserName, roomId,platformType);
        sendData.code = pageInfo.resultCode;
        sendData.data = Lists.newArrayList(pageInfo.getContent());
        sendData.count = (int) pageInfo.getTotal();
        return sendData;
    }

    @RequestMapping("/getBetRecordSummary")
    @ResponseBody
    public BetRecordSummary  getBetRecordSummary(){
        return revenueAnalysisProcess.getBetRecordSummary();
    }

    @RequestMapping("/getPageRecordList")
    @ResponseBody
    public HttpURLConnection<DailyBetInfo> getPageBetRecordList(int page ,int size){
        PageInfo<DailyBetInfo> pageBeRecord = revenueAnalysisProcess.getPageBeRecord(page-1, size);
        var sendData = new HttpURLConnection<DailyBetInfo>();
        sendData.code = 200;
        sendData.count = (int) pageBeRecord.getTotal();
        sendData.data = Lists.newArrayList(pageBeRecord.getContent());
        return sendData;
    }


}
