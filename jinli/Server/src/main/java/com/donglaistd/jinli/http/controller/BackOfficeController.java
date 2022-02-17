package com.donglaistd.jinli.http.controller;

import com.donglaistd.jinli.database.dao.BackOfficeUserRepository;
import com.donglaistd.jinli.http.entity.BackOfficeUserSend;
import com.donglaistd.jinli.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.logging.Logger;

@Controller
@RequestMapping("/backOffice")
public class BackOfficeController {
    private static final Logger logger = Logger.getLogger(BackOfficeController.class.getName());

    @Autowired
    BackOfficeUserRepository backOfficeUserRepository;

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    VerifyUtil verifyUtil;

    @RequestMapping("/home/index")
    @ResponseBody
    public Object handleRequest(HttpServletRequest request, HttpServletResponse response, String userId, String code, String name, Principal principal) {
       /* if(principal!=null && verifyUtil.isPlatformAccount(principal.getName())){
            logger.info("api account login success");
            return "success";
        }*/
        logger.info("userid" + userId);
        logger.fine("code" + code);
        logger.fine("name" + name);
        ModelAndView modelAndView = new ModelAndView("index.html");
        modelAndView.addObject("message", "success");
        return modelAndView;
    }



    @RequestMapping("/home/liveDataSummary")
    @ResponseBody
    public ModelAndView liveDataSummary(HttpServletRequest request,HttpServletResponse response){
        ModelAndView modelAndView = new ModelAndView("liveDataSummary.html");
        return modelAndView;
    }

    @RequestMapping("/home/userDataSummary")
    @ResponseBody
    public ModelAndView userDataSummary(HttpServletRequest request,HttpServletResponse response){
        ModelAndView modelAndView = new ModelAndView("userDataSummary.html");
        return modelAndView;
    }

    @RequestMapping("/home/overviewToday")
    @ResponseBody
    public ModelAndView overviewToday(HttpServletRequest request,HttpServletResponse response){
        ModelAndView modelAndView = new ModelAndView("overviewToday.html");
        return modelAndView;
    }


    @RequestMapping("/ban_user")
    public Object banUser(HttpServletRequest request, HttpServletResponse response, String userId) {
        logger.fine("userid" + userId);
        return "success";
    }

    @RequestMapping("/backOfficeManagement")
    @ResponseBody
    public ModelAndView backOfficeManagement(HttpServletRequest request,HttpServletResponse response){
        ModelAndView modelAndView = new ModelAndView("backOfficeManagement/backOfficeManagement.html");
        return modelAndView;
    }

    @RequestMapping("/getBackOfficeMsg")
    @ResponseBody
    public Object getBackOfficeMsg(HttpServletRequest request, HttpServletResponse response, Principal principal) {
        var objMsg = backOfficeUserRepository.findByAccountName(principal.getName());
        var send = new BackOfficeUserSend();
        send.id = objMsg.getId();
        send.accountName = objMsg.getAccountName();
        send.role = objMsg.getRoles();
        return send;
    }

}
