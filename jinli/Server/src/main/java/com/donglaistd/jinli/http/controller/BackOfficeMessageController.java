package com.donglaistd.jinli.http.controller;


import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.dao.system.SystemMessageConfigDaoService;
import com.donglaistd.jinli.database.entity.backoffice.ChangeRollMessageRecord;
import com.donglaistd.jinli.database.entity.backoffice.ChangeSystemTipMessageRecord;
import com.donglaistd.jinli.database.entity.system.SystemMessageConfig;
import com.donglaistd.jinli.database.entity.system.carousel.BaseCarousel;
import com.donglaistd.jinli.database.entity.system.carousel.GuessCarousel;
import com.donglaistd.jinli.database.entity.system.carousel.LiveCarousel;
import com.donglaistd.jinli.database.entity.system.carousel.RaceCarousel;
import com.donglaistd.jinli.http.entity.CarouselRequest;
import com.donglaistd.jinli.http.entity.HttpURLConnection;
import com.donglaistd.jinli.http.entity.PageInfo;
import com.donglaistd.jinli.service.SystemMessageConfigProcess;
import com.donglaistd.jinli.util.PlatformUtil;
import com.donglaistd.jinli.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("backOffice/backOfficeMessage/")
public class BackOfficeMessageController {
    @Autowired
    SystemMessageConfigProcess systemMessageService;
    @Autowired
    SystemMessageConfigDaoService systemMessageConfigDaoService;

    @RequestMapping("/sendBackOfficeMessage")
    @ResponseBody
    public ModelAndView sendBackOfficeMessage(HttpServletRequest request, HttpServletResponse response) {
        SystemMessageConfig systemMessageConfig = systemMessageConfigDaoService.findSystemMessage(Constant.PlatformType.PLATFORM_JINLI);
        ModelAndView modelAndView = new ModelAndView("backOfficeMessage/sendBackOfficeMessage.html");
        if(systemMessageConfig != null){
            modelAndView.addObject("message", systemMessageConfig.getRollMessage());
        }
        return modelAndView;
    }

    @RequestMapping("/messageSettings")
    @ResponseBody
    public ModelAndView messageSettings(HttpServletRequest request, HttpServletResponse response) {
        SystemMessageConfig systemMessageConfig = systemMessageConfigDaoService.findSystemMessage(Constant.PlatformType.PLATFORM_JINLI);
        ModelAndView modelAndView =  new ModelAndView("backOfficeMessage/messageSettings.html");
        modelAndView.addObject("message", systemMessageConfig.getSystemTipMessage());
        return modelAndView;
    }



    @RequestMapping("/sendBackOfficeMessageRecord")
    @ResponseBody
    public ModelAndView sendBackOfficeMessageRecord(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("backOfficeMessage/sendBackOfficeMessageRecord.html");

    }

    @RequestMapping("/messageSettingsRecord")
    @ResponseBody
    public ModelAndView messageSettingsRecord(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("backOfficeMessage/messageSettingsRecord.html");
    }

    @RequestMapping("/guessCarousel")
    @ResponseBody
    public ModelAndView guessCarousel(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView =  new ModelAndView("backOfficeMessage/guessCarousel.html");
        return modelAndView;
    }

    @RequestMapping("/liveCarousel")
    @ResponseBody
    public ModelAndView liveCarousel(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView =  new ModelAndView("backOfficeMessage/liveCarousel.html");
        return modelAndView;
    }

    @RequestMapping("/matchCarousel")
    @ResponseBody
    public ModelAndView matchCarousel(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView =  new ModelAndView("backOfficeMessage/matchCarousel.html");
        return modelAndView;
    }

    @RequestMapping("/addGuessCarousel")
    @ResponseBody
    public ModelAndView addGuessCarousel(String id) {
        ModelAndView modelAndView =  new ModelAndView("backOfficeMessage/addGuessCarousel.html");
        if(!StringUtils.isNullOrBlank(id)){
            GuessCarousel carousel = (GuessCarousel) systemMessageService.findByIdAndCarouselType(id, Constant.CarouselType.GUESS_CAROUSEL);
            modelAndView.addObject("data", carousel);
        }else{
            modelAndView.addObject("data", new GuessCarousel());
        }
        return modelAndView;
    }

    @RequestMapping("/addLiveCarousel")
    @ResponseBody
    public ModelAndView addLiveCarousel(HttpServletRequest request, HttpServletResponse response,String id) {
        ModelAndView modelAndView =  new ModelAndView("backOfficeMessage/addLiveCarousel.html");
        if(!StringUtils.isNullOrBlank(id)){
            LiveCarousel carousel = (LiveCarousel) systemMessageService.findByIdAndCarouselType(id, Constant.CarouselType.LIVE_CAROUSEL);
            modelAndView.addObject("data", carousel);
        }else{
            modelAndView.addObject("data", new LiveCarousel());
        }
        return modelAndView;
    }

    @RequestMapping("/addMatchCarousel")
    @ResponseBody
    public ModelAndView addMatchCarousel(HttpServletRequest request, HttpServletResponse response,String id) {
        ModelAndView modelAndView =  new ModelAndView("backOfficeMessage/addMatchCarousel.html");
        if(!StringUtils.isNullOrBlank(id)){
            RaceCarousel carousel = (RaceCarousel) systemMessageService.findByIdAndCarouselType(id, Constant.CarouselType.RACE_CAROUSEL);
            modelAndView.addObject("carousel", carousel);
        }else{
            modelAndView.addObject("carousel", new RaceCarousel());
        }
        return modelAndView;
    }

    @RequestMapping("/sendRollMessage")
    @ResponseBody
    public Object sendRollMessage(String message, long intervalTime, long startTime, long endTime, int platform, Principal principal) {
        if(message!=null) message = message.trim();
        Constant.PlatformType platformType = Constant.PlatformType.forNumber(platform);
        if(startTime >= endTime || intervalTime <= 0)
            return "timeParamError";
        else if(Objects.isNull(platformType))
            return "platformError";
        else if(Objects.equals( Constant.PlatformType.PLATFORM_DEFAULT,platformType)){
            //全平台
            for (Constant.PlatformType type : PlatformUtil.getAllPlatform())
                systemMessageService.updateSystemRollMessageConfig(message,intervalTime,startTime,endTime, type,principal.getName());
        }else{
            systemMessageService.updateSystemRollMessageConfig(message,intervalTime,startTime,endTime, platformType,principal.getName());
        }
        return  "success";
    }

    @RequestMapping("/tipsMessage")
    @ResponseBody
    public String tipsMessage(String message, int platform,Principal principal) {
        if(message!=null) message = message.trim();
        Constant.PlatformType platformType = Constant.PlatformType.forNumber(platform);
        if(Objects.isNull(platformType)) return "error";
        else if(Objects.equals( Constant.PlatformType.PLATFORM_DEFAULT,platformType)){
            //全平台修改
            for (Constant.PlatformType type : PlatformUtil.getAllPlatform())
                systemMessageService.setTipsMessage(type, message, principal.getName());
        }else{
            systemMessageService.setTipsMessage(platformType, message, principal.getName());
        }
       return "success";
    }

    //查询平台消息
    @RequestMapping("/platformMessage")
    @ResponseBody
    public  SystemMessageConfig queryPlatformMessage(int platform) {
        Constant.PlatformType platformType = Constant.PlatformType.forNumber(platform);
        if(Objects.isNull(platformType)) return null;
        return systemMessageConfigDaoService.findSystemMessage(platformType);
    }

    //CarouselConfig  Api

    @RequestMapping("/findCarousel")
    @ResponseBody
    public BaseCarousel findCarousel(String id, Constant.CarouselType carouselType){
        return systemMessageService.findByIdAndCarouselType(id,carouselType);
    }

    @RequestMapping("/getRaceFree")
    @ResponseBody
    public List<Integer> getRaceFree(Constant.RaceType raceType){
        return systemMessageService.getRacesFreeByRaceType(raceType);
    }

    @RequestMapping("/addCarousel")
    @ResponseBody
    public HttpURLConnection<Object> addCarousel(CarouselRequest request){
        Constant.ResultCode resultCode = systemMessageService.createCarousel(request);
        int code = 200;
        if (!Objects.equals(Constant.ResultCode.SUCCESS, resultCode)) {
            code = 500;
        }
        return new HttpURLConnection<>(code, resultCode.toString().toLowerCase());
    }

    @RequestMapping("/updateCarousel")
    @ResponseBody
    public HttpURLConnection<Object> updateCarousel(CarouselRequest request){
        Constant.ResultCode resultCode = systemMessageService.updateCarousel(request);
        int code = Objects.equals(Constant.ResultCode.SUCCESS, resultCode) ? 200 : 500;
        return new HttpURLConnection<>(code,resultCode.toString().toLowerCase());
    }

    @RequestMapping("/removeCarousel")
    @ResponseBody
    public HttpURLConnection<Object>  removeCarousel(CarouselRequest request){
        Constant.ResultCode resultCode = systemMessageService.removeCarousel(request.getId(), request.getCarouselType());
        int code = Objects.equals(Constant.ResultCode.SUCCESS, resultCode) ? 200 : 500;
        return new HttpURLConnection<>(code,resultCode.toString().toLowerCase());
    }

    @RequestMapping("/getCarouselData")
    @ResponseBody
    public List<? extends BaseCarousel> getCarouselData(Constant.CarouselType carouselType){
        return systemMessageService.getCarouselByType(carouselType);
    }

    @RequestMapping("/rollMessageRecord")
    @ResponseBody
    public PageInfo<ChangeRollMessageRecord> rollMessageRecord(int page,int size,int platform){
        return systemMessageService.findChangeRollMessageRecord(page-1, size, Constant.PlatformType.forNumber(platform));
    }

    @RequestMapping("/tipMessageRecord")
    @ResponseBody
    public PageInfo<ChangeSystemTipMessageRecord> tipMessageRecord(int page, int size, int platform){
        return systemMessageService.findChangeSystemTipMessageRecord(page-1, size, Constant.PlatformType.forNumber(platform));
    }

    @RequestMapping("/cleanRollMessage")
    @ResponseBody
    public HttpURLConnection<String> tipMessageRecord(int platform, Principal principal){
        systemMessageService.cleanRollMessage(Constant.PlatformType.forNumber(platform),principal.getName());
        return new HttpURLConnection<>(200,"success");
    }
}
