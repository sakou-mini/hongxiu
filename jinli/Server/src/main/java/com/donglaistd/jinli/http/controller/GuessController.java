package com.donglaistd.jinli.http.controller;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.dao.GuessDaoService;
import com.donglaistd.jinli.database.dao.GuessWagerRecordDaoService;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.dao.system.SystemMessageConfigDaoService;
import com.donglaistd.jinli.database.entity.backoffice.Guess;
import com.donglaistd.jinli.database.entity.backoffice.GuessWagerRecord;
import com.donglaistd.jinli.database.entity.system.SystemMessageConfig;
import com.donglaistd.jinli.database.entity.system.carousel.GuessCarousel;
import com.donglaistd.jinli.database.entity.system.carousel.LiveCarousel;
import com.donglaistd.jinli.database.entity.system.carousel.RaceCarousel;
import com.donglaistd.jinli.http.entity.*;
import com.donglaistd.jinli.http.service.CoinFlowService;
import com.donglaistd.jinli.service.SystemMessageConfigProcess;
import com.donglaistd.jinli.service.statistic.LiveUserManagerPageProcess;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.GuessUtil;
import com.donglaistd.jinli.util.StringUtils;
import com.donglaistd.jinli.util.TimeUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;


@Controller
@RequestMapping("backOffice/guess/")
public class GuessController {
    private static final Logger logger = Logger.getLogger(GuessController.class.getName());
    @Autowired
    GuessDaoService guessDaoService;
    @Autowired
    GuessUtil guessUtil;
    @Autowired
    GuessWagerRecordDaoService guessWagerRecordDaoService;
    @Autowired
    UserDaoService userDaoService;
    @Autowired
    DataManager dataManager;
    @Autowired
    CoinFlowService coinFlowService;
    @Autowired
    LiveUserManagerPageProcess liveUserManagerPageProcess;



    @Autowired
    SystemMessageConfigProcess systemMessageService;
    @Autowired
    SystemMessageConfigDaoService systemMessageConfigDaoService;


    @RequestMapping("/liveUserRoom")
    @ResponseBody
    public Object liveUserRoom(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView("LiveUser/liveUserRoom.html");
        modelAndView.addObject("message", "success");
        return modelAndView;
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

    @RequestMapping("/giftList")
    @ResponseBody
    public ModelAndView giftList() {
        return new ModelAndView("Gift/giftList.html");
    }

    @RequestMapping("/indexJinli")
    @ResponseBody
    public ModelAndView indexJinli() {
        return new ModelAndView("jinli/indexJinli.html");
    }

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


    @RequestMapping("/liveUserListJinLi")
    @ResponseBody
    public ModelAndView liveUserListJinLi(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("jinli/liveUserListJinLi.html");

    }

    @RequestMapping("/liveUserJinLiDetailInfo")
    @ResponseBody
    public ModelAndView liveUserJinLiDetailInfo(String liveUserId) {
        ModelAndView modelAndView = new ModelAndView("jinli/liveUserJinLiDetailInfo.html");
        if (liveUserId == null) {
            return modelAndView;
        }
        modelAndView.addObject("data", liveUserManagerPageProcess.getLiveUserDetail(liveUserId));
        return modelAndView;
    }

    @RequestMapping("/messageSettingsRecord")
    @ResponseBody
    public ModelAndView messageSettingsRecord(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("backOfficeMessage/messageSettingsRecord.html");
    }





    @RequestMapping("/getGuessByTime")
    @ResponseBody
    public Object getGuessByTime(HttpServletRequest request, HttpServletResponse response, Long startTime, Long endTime, int guessType, String sortItem, int page, int size) {
        Long nowTime = System.currentTimeMillis();
        var sendData = new HttpURLConnection();
        if (guessType == -1) {
            PageImpl<Guess> guessPageObj = guessDaoService.findStatueAndGuessTypeAndTime(startTime, endTime, null, null, sortItem, page - 1, size, nowTime);
            var guessList = guessPageObj.getContent();
            var sendList = new ArrayList<>();
            for (int i = 0; i < guessList.size(); i++) {
                sendList.add(guessUtil.judgeGuessState(guessUtil.judgeGuessShow(guessList.get(i))));
            }
            sendData.code = 0;
            sendData.data = sendList;
            sendData.count = (int) guessPageObj.getTotalElements();
            return sendData;
        } else {
            PageImpl<Guess> guessPageObj = guessDaoService.findStatueAndGuessTypeAndTime(startTime, endTime, null, Constant.GuessType.forNumber(guessType), sortItem, page - 1,
                    size, nowTime);
            var guessList = guessPageObj.getContent();
            var sendList = new ArrayList<>();
            for (int i = 0; i < guessList.size(); i++) {
                sendList.add(guessUtil.judgeGuessState(guessUtil.judgeGuessShow(guessList.get(i))));
            }
            sendData.code = 0;
            sendData.data = sendList;
            sendData.count = (int) guessPageObj.getTotalElements();
            return sendData;
        }
    }

    @RequestMapping("/addTestGuess")
    @ResponseBody
    public Object addNewGuess(){
        long nowTime = System.currentTimeMillis();
        long showEndTime = nowTime + TimeUnit.DAYS.toMillis(1);
        long wagerStartTime = nowTime + 1;
        long wagerEndTime = nowTime + TimeUnit.DAYS.toMillis(1) - 1;
        String title = "US presidential campaign";
        String subtitle = "US presidential campaign";
        Constant.GuessType guessType = Constant.GuessType.POLITICS;
        var guessItems = new ArrayList<GuessItem>();
        GuessItem item1 = new GuessItem();
        item1.optionContent = "Trump";
        guessItems.add(item1);
        GuessItem item2 = new GuessItem();
        item1.optionContent = "Biden";
        guessItems.add(item2);

        Guess guess = Guess.newInstance(title, subtitle, guessItems, guessType, nowTime, showEndTime, wagerStartTime, wagerEndTime);
        guess.setGuessImg("");
        guess.setSort(1);
        guess.setWindowImg("");
        guessDaoService.save(guess);
        return "success";
    }

    @RequestMapping("/lottery/addNewGuess")
    @ResponseBody
    public Object addNewGuess(Long bet,String guessImg,int guessType,String itemList,Long showEndTime,Long showStartTime,String subtitle, String title,
                              Long wagerEndTime, Long wagerStartTime, String windowImg, int sort) {

        if(!Objects.isNull(guessImg) && !Objects.isNull(windowImg)){
            if(!guessImg.startsWith("/")){
                guessImg = "/"+guessImg;
            }
            if(!windowImg.startsWith("/")){
                windowImg = "/"+windowImg;
            }
        }
        var guessItems = new ArrayList<GuessItem>();
        Long nowTime = System.currentTimeMillis();
        var sendObj = new HttpURLConnection<>();
        if (showStartTime < nowTime || showEndTime < nowTime || wagerStartTime < nowTime || wagerEndTime < nowTime) {
            sendObj.code = 405;
            return sendObj;
        }
        Gson gson = new Gson();
        List<String> list = gson.fromJson(itemList, new TypeToken<List<String>>() {
        }.getType());
        list.forEach(content -> guessItems.add(new GuessItem(content)));
        Guess guess = Guess.newInstance(title, subtitle, guessItems, Constant.GuessType.forNumber(guessType), showStartTime, showEndTime, wagerStartTime, wagerEndTime);
        guess.setGuessImg(guessImg);
        guess.setSort(sort);
        guess.setWindowImg(windowImg);
        guessDaoService.save(guess);
        sendObj.code = 200;
        return sendObj;
    }

    @RequestMapping("/lottery/guessEdit")
    @ResponseBody
    public Object guessEdit(HttpServletRequest request, HttpServletResponse response,
                            String id, Long bet,
                            String guessImg,
                            int guessType,
                            String itemList,
                            Long showEndTime,
                            Long showStartTime,
                            String subtitle,
                            String title,
                            Long wagerEndTime,
                            Long wagerStartTime,
                            String windowImg,
                            int sort) {
        var saveItemList = new ArrayList<GuessItem>();
        Gson gson = new Gson();
        List<String> list = gson.fromJson(itemList, new TypeToken<List<String>>() {
        }.getType());
        for (int i = 0; i < list.size(); i++) {
            GuessItem item = new GuessItem();
            item.optionContent = list.get(i);
            saveItemList.add(item);
        }
        if(!Objects.isNull(guessImg) && !Objects.isNull(windowImg)){
            if(!guessImg.startsWith("/")){
                guessImg = "/"+guessImg;
            }
            if(!windowImg.startsWith("/")){
                windowImg = "/"+windowImg;
            }
        }
        var saveGuess = guessDaoService.findById(id);
        saveGuess.setWagerEndTime(wagerEndTime);
        saveGuess.setWagerStartTime(wagerStartTime);
        saveGuess.setShowEndTime(showEndTime);
        saveGuess.setShowStartTime(showStartTime);
        saveGuess.setSubtitle(subtitle);
        saveGuess.setTitle(title);
        saveGuess.setGuessType(Constant.GuessType.forNumber(guessType));
        saveGuess.setItemList(saveItemList);
        saveGuess.setGuessImg(guessImg);
        saveGuess.setSort(sort);
        saveGuess.setWindowImg(windowImg);
        guessDaoService.save(saveGuess);
        return null;
    }

    @RequestMapping("/getNotStartGuess")
    @ResponseBody
    public Object getNotStartGuess(HttpServletRequest request, HttpServletResponse response,
                                   Long startTime, Long endTime, int guessType, String sortItem, int page, int size) {
        Long nowTime = System.currentTimeMillis();
        var sendData = new HttpURLConnection();
        if (guessType == -1) {
            PageImpl<Guess> guessPageObj = guessDaoService.findByIsShow(startTime, endTime, Constant.GuessShow.INVISIBLE, null, sortItem, page - 1, size, nowTime);
            var guessList = guessPageObj.getContent();
            var sendList = new ArrayList<>();
            for (int i = 0; i < guessList.size(); i++) {
                sendList.add(guessUtil.judgeGuessState(guessUtil.judgeGuessShow(guessList.get(i))));
            }
            sendData.code = 0;
            sendData.data = sendList;
            sendData.count = (int) guessPageObj.getTotalElements();
            return sendData;
        } else {
            PageImpl<Guess> guessPageObj = guessDaoService.findByIsShow(startTime, endTime, Constant.GuessShow.INVISIBLE, Constant.GuessType.forNumber(guessType), sortItem,
                    page - 1, size, nowTime);
            var guessList = guessPageObj.getContent();
            var sendList = new ArrayList<>();
            for (Guess guess : guessList) {
                sendList.add(guessUtil.judgeGuessState(guessUtil.judgeGuessShow(guess)));
            }
            sendData.code = 0;
            sendData.data = sendList;
            sendData.count = (int) guessPageObj.getTotalElements();
            return sendData;
        }
    }

    @RequestMapping("/getShowOverGuess")
    @ResponseBody
    public Object getShowOverGuess(HttpServletRequest request, HttpServletResponse response,
                                   Long startTime, Long endTime, int guessType, String sortItem, int page, int size) {
        Long nowTime = System.currentTimeMillis();
        var sendData = new HttpURLConnection();
        if (guessType == -1) {
            PageImpl<Guess> guessPageObj = guessDaoService.findByShowOverGuess(startTime, endTime, null, sortItem, page - 1, size, nowTime);
            var guessList = guessPageObj.getContent();
            var sendList = new ArrayList<>();
            for (int i = 0; i < guessList.size(); i++) {
                sendList.add(guessUtil.judgeGuessState(guessUtil.judgeGuessShow(guessList.get(i))));
            }
            sendData.code = 0;
            sendData.data = sendList;
            sendData.count = (int) guessPageObj.getTotalElements();
            return sendData;
        } else {
            PageImpl<Guess> guessPageObj = guessDaoService.findByShowOverGuess(startTime, endTime, Constant.GuessType.forNumber(guessType), sortItem, page - 1, size, nowTime);
            var guessList = guessPageObj.getContent();
            var sendList = new ArrayList<>();
            for (int i = 0; i < guessList.size(); i++) {
                sendList.add(guessUtil.judgeGuessState(guessUtil.judgeGuessShow(guessList.get(i))));
            }
            sendData.code = 0;
            sendData.data = sendList;
            sendData.count = (int) guessPageObj.getTotalElements();
            return sendData;
        }
    }

    @RequestMapping("/getWagerOverGuess")
    @ResponseBody
    public Object getWagerOverGuess(HttpServletRequest request, HttpServletResponse response,
                                    Long startTime, Long endTime, int guessType, String sortItem, int page, int size) {
        Long nowTime = System.currentTimeMillis();
        var sendData = new HttpURLConnection();
        if (guessType == -1) {
            PageImpl<Guess> guessPageObj = guessDaoService.findByWagerOverGuess(startTime, endTime, null, sortItem, page - 1, size, nowTime);
            var guessList = guessPageObj.getContent();
            var sendList = new ArrayList<>();
            for (int i = 0; i < guessList.size(); i++) {
                sendList.add(guessUtil.judgeGuessState(guessUtil.judgeGuessShow(guessList.get(i))));
            }
            sendData.code = 0;
            sendData.data = sendList;
            sendData.count = (int) guessPageObj.getTotalElements();
            return sendData;
        } else {
            PageImpl<Guess> guessPageObj = guessDaoService.findByWagerOverGuess(startTime, endTime, Constant.GuessType.forNumber(guessType), sortItem, page - 1, size, nowTime);
            var guessList = guessPageObj.getContent();
            var sendList = new ArrayList<>();
            for (int i = 0; i < guessList.size(); i++) {
                sendList.add(guessUtil.judgeGuessState(guessUtil.judgeGuessShow(guessList.get(i))));
            }
            sendData.code = 0;
            sendData.data = sendList;
            sendData.count = (int) guessPageObj.getTotalElements();
            return sendData;
        }
    }

    @RequestMapping("/lottery/getWaitPrizeList")
    @ResponseBody
    public Object getWaitPrizeList(HttpServletRequest request, HttpServletResponse response, int page, int size) {
        Long nowTime = System.currentTimeMillis();
        var waitDrawPage = guessDaoService.findByTimeLteNowTime(nowTime, page - 1, size);
        var waitDrawList = waitDrawPage.getContent();
        var sendData = new ArrayList<>();
        for (int i = 0; i < waitDrawList.size(); i++) {
            var sendObj = new GuessWaitDraw();
            var total = guessWagerRecordDaoService.countByGuessId(waitDrawList.get(i).getId());
            sendObj.id = waitDrawList.get(i).getId();
            sendObj.total = total;
            sendObj.title = waitDrawList.get(i).getTitle();
            sendObj.showEndTime = waitDrawList.get(i).getShowEndTime();
            sendObj.totalCoin = waitDrawList.get(i).getTotalCoin();
            sendObj.profit = waitDrawList.get(i).getTotalCoin() / 10;
            sendObj.wagerEndTime = waitDrawList.get(i).getWagerEndTime();
            sendObj.wagerStartTime = waitDrawList.get(i).getWagerStartTime();
            sendData.add(sendObj);
        }
        var send = new HttpURLConnection();
        send.data = sendData;
        send.code = 0;
        send.count = (int) waitDrawPage.getTotalElements();
        return send;
    }

    @RequestMapping("/lottery/drawPrize")
    @ResponseBody
    public Object drawPrize(HttpServletRequest request, HttpServletResponse response, String guessId, String win) {
        logger.fine("start time");
        guessUtil.settleGuess(guessId, win);
        logger.fine("end time");
        return null;
    }

    @RequestMapping("/lottery/getGuessWagerListByGuessId")
    @ResponseBody
    public Object getGuessWagerListByGuessId(HttpServletRequest request, HttpServletResponse response, String id, int page, int size) {
        var guessWagerListPage = guessWagerRecordDaoService.findByGuessIdAndPage(id, page - 1, size);
        var guessWagerList = guessWagerListPage.getContent();
        List<GuessWagerDetails> sendList = new ArrayList<>();
        for (int i = 0; i < guessWagerListPage.getContent().size(); i++) {
            var guessWagerDetails = new GuessWagerDetails();
            guessWagerDetails.displayName = userDaoService.findById(guessWagerList.get(i).getUserId()).getDisplayName();
            guessWagerDetails.orderNum = guessWagerList.get(i).getOrderNum();
            guessWagerDetails.userId = guessWagerList.get(i).getUserId();
            guessWagerDetails.setWagerList(guessWagerList.get(i).getWagerList());
            guessWagerDetails.wagerTime = guessWagerList.get(i).getWagerTime();
            guessWagerDetails.totalCoin = guessWagerList.get(i).getTotal();
            sendList.add(guessWagerDetails);
        }
        var send = new HttpURLConnection();
        send.count = (int) guessWagerListPage.getTotalElements();
        send.data = sendList;
        send.code = 0;
        return send;
    }

    @RequestMapping("/lottery/getGuessStatisticsList")
    @ResponseBody
    public Object getGuessStatisticsList(HttpServletRequest request, HttpServletResponse response, int page, int size) {
        Long nowTime = System.currentTimeMillis();
        var guessPage = guessDaoService.findByWagerStartTime(nowTime, page - 1, size);
        var guessList = guessPage.getContent();
        var sendArr = new ArrayList<GuessStatistics>();
        var send = new HttpURLConnection();
        for (int i = 0; i < guessList.size(); i++) {
            var guess = guessUtil.judgeGuessState(guessUtil.judgeGuessShow(guessList.get(i)));
            var sendGuess = new GuessStatistics();
            sendGuess.setId(guess.getId());
            sendGuess.setState(guess.getState());
            sendGuess.setTitle(guess.getTitle());
            sendGuess.setTotal(guess.getTotal());
            sendGuess.setTotalCoin(guess.getTotalCoin());
            sendGuess.setWagerEndTime(guess.getWagerEndTime());
            sendGuess.setWagerStartTime(guess.getWagerStartTime());
            sendArr.add(sendGuess);
        }
        send.code = 0;
        send.data = sendArr;
        send.count = (int) guessPage.getTotalElements();
        return send;
    }

    public void saveGuessProfitLoss(GuessWagerRecord guessOderMsg, String win) {
        guessOderMsg.getWagerList().forEach((k, v) -> {
            if (!k.equals(win)) {
                var lossNum = guessOderMsg.getWagerList().get(k).getBetNum();
                guessOderMsg.getWagerList().get(k).setProfitLoss(-lossNum);
                guessWagerRecordDaoService.save(guessOderMsg);
            }
        });
    }
}
