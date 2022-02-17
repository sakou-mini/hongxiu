package com.donglaistd.jinli.service;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.database.entity.system.Menu;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@ActiveProfiles("test")
public class MenuProcessTest  extends BaseTest {
    @Autowired
    MenuProcess menuProcess;

    @Test
    public void menuChildListTest(){
        List<Menu> allChildrenMenuList = menuProcess.getAllChildrenMenuList();
        System.out.println(allChildrenMenuList);
    }
}
