package com.donglai.web.process;

import com.donglai.web.WebBaseTest;
import com.donglai.web.db.backoffice.service.MenuService;
import com.donglai.web.db.backoffice.service.RoleService;
import com.donglai.web.process.permission.MenuProcess;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuProcessTest extends WebBaseTest {
    @Autowired
    MenuProcess menuProcess;
    @Autowired
    MenuService menuService;
    @Autowired
    RoleService roleService;

    @Test
    public void test(){
     /*   Role rootRole = roleService.findByRoleName(BackOfficeRole.ROLE_ADMIN.name());
        List<Menu> roleMenuPageList = menuProcess.getRoleMenuPageList(Lists.newArrayList(rootRole),MenuType.MENU_PAGE_SYSTEM);
        List<Menu> menusByMenuTypeAndMenuRole = menuService.findMenusByMenuTypeAndMenuRole(MenuType.MENU_API, Lists.newArrayList(rootRole));*/
    }
    @Autowired
    WebApplicationContext applicationContext;

    @Test
    public void getAllUrlTest(){
        RequestMappingHandlerMapping mapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = mapping.getHandlerMethods();
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> m : handlerMethods.entrySet()) {
            Map<String, String> map1 = new HashMap<String, String>();
            RequestMappingInfo info = m.getKey();
            HandlerMethod method = m.getValue();
            PatternsRequestCondition p = info.getPatternsCondition();
            for (String url : p.getPatterns()) {
                map1.put("url", url);
            }
            map1.put("className", method.getMethod().getDeclaringClass().getName()); // 类名
            map1.put("method", method.getMethod().getName()); // 方法名
            RequestMethodsRequestCondition methodsCondition = info.getMethodsCondition();
            for (RequestMethod requestMethod : methodsCondition.getMethods()) {
                map1.put("type", requestMethod.toString());
            }
            list.add(map1);
        }
        System.out.println(list);
    }

}
