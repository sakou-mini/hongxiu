package com.donglaistd.jinli.http.controller;

import com.donglaistd.jinli.util.WordFilterUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("word")
public class WordFilterController {
    @Autowired
    WordFilterUtil wordFilterUtil;

    @Value("${jinli.config.keywords.path}")
    private String keyWordPath;

    @RequestMapping("/initWordLibrary")
    @ResponseBody
    public String initWordFilter(){
        wordFilterUtil.initWordLibrary(keyWordPath);
        return "success";
    }
}
