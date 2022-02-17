package com.donglaistd.jinli.config.security;

import com.donglaistd.jinli.database.entity.backoffice.BackOfficeRole;
import com.donglaistd.jinli.database.entity.system.Menu;
import com.donglaistd.jinli.service.MenuProcess;
import com.donglaistd.jinli.service.MenuRoleProcess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

import static com.donglaistd.jinli.constant.BackOfficeConstant.ROLE_PREFIX;

@Service
public class MyInvocationSecurityMetadataSourceService implements FilterInvocationSecurityMetadataSource {

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();
    @Autowired
    MenuRoleProcess menuRoleProcess;

    private Map<String, Collection<ConfigAttribute>> map =null;

    public void loadResourceDefine() {
        //此处只添加了用户的名字，其实还可以添加更多权限的信息，例如请求方法到ConfigAttribute的集合中去。此处添加的信息将会作为MyAccessDecisionManager类的decide的第三个参数。
        map = menuRoleProcess.getMenuRolePathPermissionMap();
        map.forEach((k,v)->{
            v.add(new SecurityConfig(ROLE_PREFIX + BackOfficeRole.ADMIN.name()));
        });
    }

    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        if(map ==null) loadResourceDefine();
        HttpServletRequest request = ((FilterInvocation) object).getHttpRequest();
        for (String resUrl : map.keySet()) {
            if (antPathMatcher.match(resUrl, request.getRequestURI().toString())) {
                return map.get(resUrl);
            }
        }
        return null;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }
}
