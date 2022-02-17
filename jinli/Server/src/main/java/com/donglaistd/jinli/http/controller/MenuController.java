package com.donglaistd.jinli.http.controller;

import com.donglaistd.jinli.database.dao.BackOfficeUserDaoService;
import com.donglaistd.jinli.database.entity.backoffice.BackOfficeUser;
import com.donglaistd.jinli.database.entity.backoffice.MenuRole;
import com.donglaistd.jinli.database.entity.system.Menu;
import com.donglaistd.jinli.http.service.BackOfficeUserService;
import com.donglaistd.jinli.service.MenuProcess;
import com.donglaistd.jinli.service.MenuRoleProcess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("backOffice/menu/")
public class MenuController {
    @Autowired
    MenuProcess menuProcess;
    @Autowired
    BackOfficeUserDaoService backOfficeUserDaoService;

    @RequestMapping("/getMenuList")
    @ResponseBody
    public List<Menu> getMenuList(Principal principal){
        BackOfficeUser backOfficeUser = backOfficeUserDaoService.findByAccountName(principal.getName());
        if(backOfficeUser==null) return new ArrayList<>();
        return menuProcess.getMenuByBackOfficeUser(backOfficeUser);
    }
}
