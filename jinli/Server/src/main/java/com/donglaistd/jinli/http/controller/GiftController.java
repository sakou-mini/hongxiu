package com.donglaistd.jinli.http.controller;


import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.system.GiftConfigDaoService;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.system.GiftConfig;
import com.donglaistd.jinli.http.entity.HttpURLConnection;
import com.donglaistd.jinli.http.entity.PageInfo;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.MessageUtil;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Objects;

@Controller
@RequestMapping("backOffice/gift")
public class GiftController {

    private final GiftConfigDaoService giftConfigDaoService;
    @Autowired
    DataManager dataManager;

    public GiftController(GiftConfigDaoService giftConfigDaoService) {
        this.giftConfigDaoService = giftConfigDaoService;
    }

    @RequestMapping("/giftJson")
    @ResponseBody
    public Object getGiftJson(Constant.PlatformType platform){
        return giftConfigDaoService.getFullGiftConfigForJSon(platform).toMap();
    }

    @RequestMapping("/giftList")
    @ResponseBody
    public ModelAndView giftList() {
        return new ModelAndView("Gift/giftList.html");
    }

    @RequestMapping("/giftConsumptionList")
    @ResponseBody
    public ModelAndView giftList(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("revenueAnalysis/giftConsumptionList.html");
    }
    @RequestMapping("/liveUserReceivedGiftList")
    @ResponseBody
    public ModelAndView liveUserReceivedGiftList(){
        return new ModelAndView("revenueAnalysis/liveUserReceivedGiftList.html");
    }


    @RequestMapping("/giftPageInfo")
    @ResponseBody
    public PageInfo<GiftConfig> giftPageInfo(boolean luxury, int page, int size,int platform){
        return giftConfigDaoService.giftConfigPageInfo(luxury, PageRequest.of(page - 1, size), Constant.PlatformType.forNumber(platform));
    }

    private void broadcastGiftConfigChange(Constant.PlatformType platform){
        Jinli.GiftConfigChangeBroadcastMessage.Builder builder = Jinli.GiftConfigChangeBroadcastMessage.newBuilder();
        User user;
        for (Map.Entry<String, Channel> channelEntry : DataManager.userChannel.entrySet()) {
            user = dataManager.findUser(channelEntry.getKey());
            if(user!=null && Objects.equals(platform,user.getPlatformType())){
                MessageUtil.sendMessage(channelEntry.getValue(),MessageUtil.buildReply(builder));
            }
        }
    }

    @RequestMapping("/changePrice")
    @ResponseBody
    public HttpURLConnection<Object> changePrice(String giftId, long price, int platform){
        Constant.PlatformType platformType = Constant.PlatformType.forNumber(platform);
        if(price < 0) return new HttpURLConnection<>(500, Constant.ResultCode.PARAM_ERROR.name().toLowerCase());
        GiftConfig gift = giftConfigDaoService.findByGiftIdAndPlatform(giftId,platformType);
        if(gift == null) return new HttpURLConnection<>(500, Constant.ResultCode.GIFT_NOT_EXIST.name().toLowerCase());
        gift.setPrice(price);
        giftConfigDaoService.save(gift);
        broadcastGiftConfigChange(platformType);
        return new HttpURLConnection<>(200, Constant.ResultCode.SUCCESS.name().toLowerCase());
    }

    @RequestMapping("/openOrCloseGift")
    @ResponseBody
    public HttpURLConnection<Object> setGiftStatue(String giftId ,boolean enable, int platform){
        Constant.PlatformType platformType = Constant.PlatformType.forNumber(platform);
        GiftConfig gift = giftConfigDaoService.findByGiftIdAndPlatform(giftId ,platformType);
        if(gift == null) return new HttpURLConnection<>(500, Constant.ResultCode.GIFT_NOT_EXIST.name().toLowerCase());
        gift.setEnable(enable);
        giftConfigDaoService.save(gift);
        broadcastGiftConfigChange(platformType);
        return new HttpURLConnection<>(200, Constant.ResultCode.SUCCESS.name().toLowerCase());
    }

    @RequestMapping("/resetGiftConfig")
    @ResponseBody
    public HttpURLConnection<Object> resetGiftConfig(){
        giftConfigDaoService.resetGift();
        return new HttpURLConnection<>(200, Constant.ResultCode.SUCCESS.name().toLowerCase());
    }
}
