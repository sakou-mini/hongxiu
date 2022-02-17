package com.donglaistd.jinli.http.controller;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.constant.DomainLine;
import com.donglaistd.jinli.constant.GameConstant;
import com.donglaistd.jinli.database.dao.BackOfficeUserDaoService;
import com.donglaistd.jinli.database.dao.BackOfficeUserRepository;
import com.donglaistd.jinli.database.dao.GuessDaoService;
import com.donglaistd.jinli.database.dao.GuessWagerRecordDaoService;
import com.donglaistd.jinli.database.dao.system.SystemMessageConfigDaoService;
import com.donglaistd.jinli.database.entity.backoffice.BackOfficeRole;
import com.donglaistd.jinli.database.entity.backoffice.BackOfficeUser;
import com.donglaistd.jinli.database.entity.backoffice.MenuRole;
import com.donglaistd.jinli.database.entity.system.LiveDomainConfig;
import com.donglaistd.jinli.database.entity.system.LiveDomainConfigRecord;
import com.donglaistd.jinli.database.entity.system.SystemMessageConfig;
import com.donglaistd.jinli.database.entity.system.carousel.GuessCarousel;
import com.donglaistd.jinli.database.entity.system.carousel.LiveCarousel;
import com.donglaistd.jinli.database.entity.system.carousel.RaceCarousel;
import com.donglaistd.jinli.database.entity.system.domainConfig.DomainConfigRecord;
import com.donglaistd.jinli.http.entity.BackOfficeUserSend;
import com.donglaistd.jinli.http.entity.DomainInfo;
import com.donglaistd.jinli.http.entity.HttpURLConnection;
import com.donglaistd.jinli.http.entity.PageInfo;
import com.donglaistd.jinli.http.service.BackOfficeUserService;
import com.donglaistd.jinli.service.DomainProcess;
import com.donglaistd.jinli.service.GameServerInfoProcess;
import com.donglaistd.jinli.service.MenuRoleProcess;
import com.donglaistd.jinli.service.SystemMessageConfigProcess;
import com.donglaistd.jinli.util.GuessUtil;
import com.donglaistd.jinli.util.StringUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Controller
@RequestMapping("backOffice/backOfficeUser/")
public class BackOfficeUserController {
    private static final Logger logger = Logger.getLogger(BackOfficeController.class.getName());

    @Autowired
    BackOfficeUserRepository backOfficeUserRepository;
    @Autowired
    BackOfficeUserDaoService backOfficeUserDaoService;

    @Autowired
    BackOfficeUserService backOfficeUserService;
    @Autowired
    GameServerInfoProcess gameServerInfoProcess;
    @Autowired
    DomainProcess domainProcess;
    @Autowired
    MenuRoleProcess menuRoleProcess;

    @Autowired
    GuessDaoService guessDaoService;
    @Autowired
    GuessUtil guessUtil;
    @Autowired
    GuessWagerRecordDaoService guessWagerRecordDaoService;
    @Autowired
    SystemMessageConfigProcess systemMessageService;
    @Autowired
    SystemMessageConfigDaoService systemMessageConfigDaoService;

    @RequestMapping("/messageSettingsQ")
    @ResponseBody
    public ModelAndView platformMessageSettings(HttpServletRequest request, HttpServletResponse response) {
        SystemMessageConfig systemMessageConfig = systemMessageConfigDaoService.findSystemMessage(Constant.PlatformType.PLATFORM_Q);
        ModelAndView modelAndView =  new ModelAndView("platformQ/messageSettingsQ.html");
        modelAndView.addObject("message", systemMessageConfig.getSystemTipMessage());
        return modelAndView;
    }

    @RequestMapping("/backOfficeMessageQ")
    @ResponseBody
    public ModelAndView sendBackOfficeMessage(HttpServletRequest request, HttpServletResponse response) {
        SystemMessageConfig systemMessageConfig = systemMessageConfigDaoService.findSystemMessage(Constant.PlatformType.PLATFORM_Q);
        ModelAndView modelAndView = new ModelAndView("platformQ/backOfficeMessageQ.html");
        if(systemMessageConfig != null){
            modelAndView.addObject("message", systemMessageConfig.getRollMessage());
        }
        return modelAndView;
    }


    @RequestMapping("/messageSettingsRecordQ")
    @ResponseBody
    public ModelAndView messageSettingsRecordQ(HttpServletRequest request, HttpServletResponse response) {
        return  new ModelAndView("platformQ/messageSettingsRecordQ.html");
    }

    @RequestMapping("/backOfficeMessageRecordQ")
    @ResponseBody
    public ModelAndView backOfficeMessageRecordQ(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("platformQ/backOfficeMessageRecordQ.html");
    }

    @RequestMapping("/domainNameSetQ")
    @ResponseBody
    public ModelAndView domainNameSetQ(HttpServletRequest request,HttpServletResponse response){
        ModelAndView modelAndView = new ModelAndView("backOfficeManagement/domainNameSetQ.html");
        return modelAndView;
    }

    @RequestMapping("/domainNameSetRecordQ")
    @ResponseBody
    public ModelAndView domainNameSetRecordQ(HttpServletRequest request,HttpServletResponse response){
        ModelAndView modelAndView = new ModelAndView("backOfficeManagement/domainNameSetRecordQ.html");
        return modelAndView;
    }

    @RequestMapping("/liveDomainNameSet")
    @ResponseBody
    public ModelAndView liveDomainNameSet(HttpServletRequest request,HttpServletResponse response){
        ModelAndView modelAndView = new ModelAndView("backOfficeManagement/liveDomainNameSet.html");
        return modelAndView;
    }

    @RequestMapping("/admin/backOfficeManagement")
    @ResponseBody
    public ModelAndView backOfficeManagement(HttpServletRequest request,HttpServletResponse response){
        ModelAndView modelAndView = new ModelAndView("backOfficeManagement/backOfficeManagement.html");
        return modelAndView;
    }

    @RequestMapping("/admin/backOfficeUserAdditions")
    @ResponseBody
    public ModelAndView buckOfficeUserAdditions(HttpServletRequest request,HttpServletResponse response){
        ModelAndView modelAndView = new ModelAndView("backOfficeManagement/backOfficeUserAdditions.html");
        return modelAndView;
    }

    @RequestMapping("/admin/theServer")
    @ResponseBody
    public ModelAndView theServer(HttpServletRequest request,HttpServletResponse response){
        ModelAndView modelAndView = new ModelAndView("backOfficeManagement/theServer.html");
        return modelAndView;
    }

    @RequestMapping("/reportList")
    @ResponseBody
    public Object reportList(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView("report/reportList.html");
        return modelAndView;
    }


    //s
    @RequestMapping("/guessList")
    @ResponseBody
    public ModelAndView guessList(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("Guess/guessList.html");
    }

    @RequestMapping("/lottery/guessDetails")
    @ResponseBody
    public ModelAndView guessDetails(HttpServletRequest request, HttpServletResponse response, String id) {
        var sendMsg = guessDaoService.findById(id);
        ModelAndView modelAndView = new ModelAndView("Guess/guessDetails.html");
        modelAndView.addObject("data", sendMsg);
        return modelAndView;

    }


    //s
    @RequestMapping("/guessNotStart")
    @ResponseBody
    public ModelAndView guessNotStart(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("Guess/guessNotStart.html");
    }

    //s
    @RequestMapping("/lottery/guessStatistics")
    @ResponseBody
    public ModelAndView guessStatistics(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("Guess/guessStatistics.html");
    }
    //s
    @RequestMapping("/guessShowOver")
    @ResponseBody
    public ModelAndView guessShowOver(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("Guess/guessShowOver.html");
    }
    //s
    @RequestMapping("/lottery/guessWaitPrize")
    @ResponseBody
    public ModelAndView guessWaitPrize(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("Guess/guessWaitPrize.html");
    }

    @RequestMapping("/guessWagerOver")
    @ResponseBody
    public ModelAndView guessWagerOver(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("Guess/guessWagerOver.html");
    }

    @RequestMapping("/lottery/guessAdditions")
    @ResponseBody
    public ModelAndView guessAdditions(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("Guess/guessAdditions.html");
    }

    @RequestMapping("/lottery/guessDrawPrize")
    @ResponseBody
    public ModelAndView guessDrawPrize(HttpServletRequest request, HttpServletResponse response, String id) {
        var guessDrawMsg = guessDaoService.findById(id);
        if (!guessDrawMsg.isSettle()) {
            guessDrawMsg = guessUtil.guessTotalCalculation(guessDrawMsg);
            guessDrawMsg.becomeSettle();
            guessDaoService.save(guessDrawMsg);
        }
        ModelAndView modelAndView = new ModelAndView("Guess/guessDrawPrize.html");
        modelAndView.addObject("guessMsg", guessUtil.judgeGuessShow(guessDrawMsg));
        return modelAndView;
    }

    @RequestMapping("/lottery/guessStatisticsDetails")
    @ResponseBody
    public ModelAndView guessStatisticsDetails(HttpServletRequest request, HttpServletResponse response, String id) {
        var guessDrawMsg = guessDaoService.findById(id);
        ModelAndView modelAndView = new ModelAndView("Guess/guessStatisticsDetails.html");
        modelAndView.addObject("guessMsg", guessUtil.judgeGuessShow(guessUtil.judgeGuessState(guessUtil.guessTotalCalculation(guessDrawMsg))));
        return modelAndView;
    }

    @RequestMapping("/lottery/guessWagerDetailsList")
    @ResponseBody
    public ModelAndView guessWagerDetailsList(HttpServletRequest request, HttpServletResponse response, String id) {
        var guessOderList = guessWagerRecordDaoService.findByGuessId(id);
        ModelAndView modelAndView = new ModelAndView("Guess/guessWagerDetailsList.html");
        modelAndView.addObject("guessOder", guessOderList);
        return modelAndView;
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

    @RequestMapping("/admin/getAllMenuRoles")
    @ResponseBody
    public List<MenuRole> getAllMenuRoles(){
        return menuRoleProcess.getMenuRoleList();
    }

    @RequestMapping("/admin/backOfficeUserEdit")
    @ResponseBody
    public ModelAndView backOfficeUserEdit(HttpServletRequest request,HttpServletResponse response,String id){
        ModelAndView modelAndView = new ModelAndView("backOfficeManagement/backOfficeUserEdit.html");
        BackOfficeUser backOfficeUser = backOfficeUserDaoService.findById(id);
        BackOfficeUserSend backOfficeUserSend = new BackOfficeUserSend();
        backOfficeUserSend.id = backOfficeUser.getId();
        backOfficeUserSend.createDate = backOfficeUser.getCreateDate();
        backOfficeUserSend.accountName = backOfficeUser.getAccountName();
        backOfficeUserSend.role = backOfficeUser.getRoles();
        backOfficeUserSend.menuRoles = menuRoleProcess.getMenuRoleByRoles(backOfficeUser.getRoles());
        modelAndView.addObject("backOfficeUser", backOfficeUserSend);
        return modelAndView;
    }

    @RequestMapping("/admin/addBackOffice")
    @ResponseBody
    public Object addBackOffice(HttpServletRequest request, HttpServletResponse response, String password, String accountName, String role, Principal principal){
        logger.fine("name:"+accountName);
        var backOfficeUserMsg = backOfficeUserRepository.findByAccountName(principal.getName());
        var backOfficeName = backOfficeUserRepository.findByAccountName(accountName);
        if(backOfficeName != null){
            return "repeat";
        }
        Gson gson=new Gson();
        List<BackOfficeRole> list= gson.fromJson(role, new TypeToken<List<BackOfficeRole>>() {}.getType());
        list.remove(null);
        backOfficeUserService.createBackOfficeUser(accountName,password,list);
        return null;
    }

    @RequestMapping("/admin/editBackOffice")
    @ResponseBody
    public Object editBackOffice(HttpServletRequest request, HttpServletResponse response, String password,String id, String accountName, String role, Principal principal){
        logger.fine("name:"+accountName);
        var backOfficeUserMsg = backOfficeUserRepository.findByAccountName(principal.getName());
        var backOffice = backOfficeUserDaoService.findById(id);
        Gson gson=new Gson();
        List<BackOfficeRole> list= gson.fromJson(role, new TypeToken<List<BackOfficeRole>>() {}.getType());
        var roleList = new HashSet<BackOfficeRole>();
        for (BackOfficeRole officeRole : list) {
            if (officeRole == null) continue;
            roleList.add(officeRole);
        }
        backOffice.setRole(roleList);
        backOfficeUserRepository.save(backOffice);
        return null;
    }


    @RequestMapping("/admin/getBackOfficeUserList")
    @ResponseBody
    public Object getBackOfficeUserList(HttpServletRequest request,HttpServletResponse response,int page ,int size,Principal principal){
        var backOfficeUserPage = backOfficeUserDaoService.findAllAndPage(page-1,size);
        BackOfficeUser backOfficeUser = backOfficeUserDaoService.findByAccountName(principal.getName());
        var sendData = new HttpURLConnection();
        List<BackOfficeUserSend> backOfficeList = new ArrayList<>();
        Set<BackOfficeRole> roles;
        for(int i=0;i<backOfficeUserPage.getContent().size();i++){
            roles = backOfficeUserPage.getContent().get(i).getRoles();
            if(!backOfficeUserPage.getContent().get(i).getAccountName().equals(backOfficeUser.getAccountName())){
                if(!roles.contains(BackOfficeRole.ADMIN) &&!roles.contains(BackOfficeRole.PLATFORM_Q) && !roles.contains(BackOfficeRole.PLATFORM)){
                    BackOfficeUserSend sendMsg = new BackOfficeUserSend();

                    sendMsg.accountName = backOfficeUserPage.getContent().get(i).getAccountName();
                    sendMsg.createDate = backOfficeUserPage.getContent().get(i).getCreateDate();
                    sendMsg.id = backOfficeUserPage.getContent().get(i).getId();
                    sendMsg.role = backOfficeUserPage.getContent().get(i).getRoles();
                    backOfficeList.add(sendMsg);
                }
            }
        }
        sendData.code = 200;
        sendData.data =backOfficeList;
        sendData.count = (int)backOfficeUserPage.getTotalElements();
        return sendData;
    }

    @RequestMapping("/admin/serverClose")
    @ResponseBody
    public Object serverClose(HttpServletRequest request, HttpServletResponse response,Principal principal) {
        var backOfficeUserMsg = backOfficeUserRepository.findByAccountName(principal.getName());
        logger.fine("BackOffice shut down the server by:"+backOfficeUserMsg.getAccountName());
        gameServerInfoProcess.stopGameServer();
        return null;
    }

    @RequestMapping("/admin/serverOpen")
    @ResponseBody
    public Object serverOpen(HttpServletRequest request, HttpServletResponse response,Principal principal) {
        var backOfficeUserMsg = backOfficeUserRepository.findByAccountName(principal.getName());
        logger.fine("BackOffice turn on the server by:"+backOfficeUserMsg.getAccountName());
        gameServerInfoProcess.resumeGameServer();
        return null;
    }

    @RequestMapping("/admin/getServerInfo")
    @ResponseBody
    public Object getServerInfo(Principal principal){
        return gameServerInfoProcess.queryGameServerInfo();
    }
    @RequestMapping("/admin/changePasswordRecord")
    @ResponseBody
    public Object changePasswordRecord(){
        ModelAndView modelAndView = new ModelAndView("backOfficeManagement/changePasswordRecord.html");
        return modelAndView;
    }

    @RequestMapping("/domainList")
    @ResponseBody
    public Map<DomainLine,List<DomainInfo>> getDomainList(int platform){
        return domainProcess.getDomainList(Constant.PlatformType.forNumber(platform));
    }

    @RequestMapping("/updateDomainName")
    @ResponseBody
    public HttpURLConnection<Object> updateDomainConfig(String oldDomainName, String newDomainName, Principal principal,int platform){
        var platformType = Constant.PlatformType.forNumber(platform);
        boolean result = domainProcess.updateDomainConfig(oldDomainName, newDomainName, principal.getName(),platformType);
        if(!result) return new HttpURLConnection<>(500, "domainError");
        return new HttpURLConnection<>(200, "success");
    }

    @RequestMapping("/addDomain")
    @ResponseBody
    public HttpURLConnection<Object> addDomain(String domain, DomainLine line, Principal principal, int platform){
        var platformType = Constant.PlatformType.forNumber(platform);
        int resultCode = domainProcess.addDomain(domain, line, principal.getName(),platformType);
        if(resultCode != GameConstant.SUCCESS)
            return new HttpURLConnection<>(500, "failed");
        return new HttpURLConnection<>(200, "success");
    }

    @RequestMapping("/domainRecordInfo")
    @ResponseBody
    public PageInfo<DomainConfigRecord> domainRecordInfo(int page, int size,int platform){
        var platformType = Constant.PlatformType.forNumber(platform);
        return domainProcess.domainConfigRecordPageInfo(page - 1, size, platformType);
    }

    @RequestMapping("/removeDomain")
    @ResponseBody
    public HttpURLConnection<Object> domainRecordInfo(String domainName, int platform){
        var platformType = Constant.PlatformType.forNumber(platform);
        domainProcess.deleteDomain(domainName,platformType);
        return new HttpURLConnection<>(200, "success");
    }

    @RequestMapping("/admin/modifyPassword")
    @ResponseBody
    public HttpURLConnection<Object> modifyPassword(String backOfficeUserId,String password){
        BackOfficeUser backOfficeUser = backOfficeUserDaoService.findById(backOfficeUserId);
        if(StringUtils.isNullOrBlank(password)) return new HttpURLConnection<>(500, "password is empty");
        if(backOfficeUser == null) return new HttpURLConnection<>(500, "user not found");
        if(backOfficeUser.getRoles().contains(BackOfficeRole.ADMIN)) return new HttpURLConnection<>(500, "admin permission not allow");
        backOfficeUserService.modifyBackOfficeUserPwd(backOfficeUser,password);
        return new HttpURLConnection<>(200, "success");
    }

    @RequestMapping("/liveDomainList")
    @ResponseBody
    public List<LiveDomainConfig> liveDomainList(){
        return domainProcess.queryLiveDomainList();
    }

    @RequestMapping("/updateLiveDomain")
    @ResponseBody
    public HttpURLConnection<Object> updateLiveDomain(Constant.LiveSourceLine line , String liveDomains, Principal principal){
        if(Objects.isNull(line) || StringUtils.isNullOrBlank(liveDomains)) return new HttpURLConnection<>(200, "liveDomainList is empty");
        //去重处理
        List<String> liveDomainList = Arrays.stream(liveDomains.split(",")).map(String::trim).distinct().collect(Collectors.toList());
        domainProcess.updateLiveDomain(line, liveDomainList, principal.getName());
        return new HttpURLConnection<>(200, "success");
    }

    @RequestMapping("/liveDomainRecord")
    @ResponseBody
    public PageInfo<LiveDomainConfigRecord> liveDomainRecord(int page, int size){
        return domainProcess.queryLiveDomainConfigRecord(page,size);
    }
}
