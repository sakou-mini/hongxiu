package com.donglaistd.jinli.http.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.logging.Logger;

@Controller
public class WebLoginAndErrorController {
    Logger logger = Logger.getLogger(WebLoginAndErrorController.class.getName());
    @RequestMapping(value = "/404",produces = {"text/html"})
    public ModelAndView errorPage404(){
        return new ModelAndView("error/error404");
    }

    @RequestMapping("/404")
    @ResponseBody
    public ResponseEntity error404(){
        return ResponseEntity.status(404).build();
    }

    @RequestMapping(value="/500",produces = {"text/html"})
    public ModelAndView errorPage500(){
        return new ModelAndView("error/error500");
    }

    @RequestMapping(value = "/500")
    @ResponseBody
    public ResponseEntity error500(){
        return ResponseEntity.status(500).build();
    }


    @RequestMapping(value="/403",produces = {"text/html"})
    public ModelAndView errorPage403(){
        return new ModelAndView("error/error403");
    }

    @RequestMapping(value="/403")
    @ResponseBody
    public ResponseEntity error403(){
        return ResponseEntity.status(403).build();
    }

    @RequestMapping(value = "/login")
    @ResponseBody
    public ModelAndView login(){
        return new ModelAndView("login.html");
    }
}
