package com.donglai.web.config.shiro;

import com.donglai.model.dto.Pair;
import com.donglai.web.config.shiro.session.MySessionManager;
import com.donglai.web.db.backoffice.service.MenuService;
import com.donglai.web.process.permission.PermissionProcess;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.crazycake.shiro.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.donglai.web.constant.ShiroSecurityConstant.*;

@Configuration
@Slf4j
public class MyShiroConfiguration {


    @Autowired
    ShiroRedisProperties shiroRedisProperties;
    @Autowired
    SecurityProperties securityProperties;
    @Autowired
    MenuService menuService;
    @Autowired
    PermissionProcess permissionProcess;

    @Bean
    public ShiroFilterFactoryBean shirFilter(SecurityManager securityManager) {
        var filterChainDefinitionMap = new LinkedHashMap<String, String>();
        var shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        Map<String, Filter> filtersMap = new LinkedHashMap<>();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        //1.过滤拦截路径
        //filterChainDefinitionMap.put(securityProperties.getLogoutApi(), AUTH_LOGOUT);
        filterChainDefinitionMap.put(securityProperties.getLoginApi(), AUTH_ANON);
        filterChainDefinitionMap.put(securityProperties.getLogoutApi(), AUTH_ANON);
        for (String path : securityProperties.getSecurityIgnoreResource())
            filterChainDefinitionMap.put(path, AUTH_ANON);
        for (String path : securityProperties.getSecurityIgnoreApi()) filterChainDefinitionMap.put(path, AUTH_ANON);
        //2.自定义的过滤器(只会校验有权限认证的地址)
        filtersMap.put(AUTH_PERMISSION, new RoleOrFilter());
        shiroFilterFactoryBean.setFilters(filtersMap);
        //3.动态权限认证
        List<Pair<String, String>> allPermissionPath = permissionProcess.getAllPermissionPath();
        allPermissionPath.forEach(permissions -> filterChainDefinitionMap.put(permissions.getLeft(), permissions.getRight()));
        //4.配置shiro默认登录界面地址，前后端分离中登录界面跳转应由前端路由控制，后台仅返回json数据
        shiroFilterFactoryBean.setLoginUrl(securityProperties.getUnAuthApi());
        //5.未授权界面;
        shiroFilterFactoryBean.setUnauthorizedUrl(securityProperties.getUnauthorizedApi());
        //6.其余所有请求均需要登录认证
        filterChainDefinitionMap.put("/**", AUTH_AUTHC);
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }


    @Bean
    public HashedCredentialsMatcher hashedCredentialsMatcher() {
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        hashedCredentialsMatcher.setHashAlgorithmName("md5");//散列算法:这里使用MD5算法;
        hashedCredentialsMatcher.setHashIterations(2);//散列的次数，比如散列两次，相当于 md5(md5(""));
        return hashedCredentialsMatcher;
    }

    /*自定义权限匹配和密码匹配器*/
    @Bean
    public MyShiroRealm myShiroRealm() {
        MyShiroRealm myShiroRealm = new MyShiroRealm();
        myShiroRealm.setCredentialsMatcher(hashedCredentialsMatcher());
        return myShiroRealm;
    }

    @Bean
    public SecurityManager securityManager(RedisCacheManager redisCacheManager,SessionManager sessionManager) {
        DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
        manager.setRealm(myShiroRealm());
        //自定义session管理
        manager.setSessionManager(sessionManager);
        // 自定义缓存实现 使用redis
        manager.setCacheManager(redisCacheManager);
        return manager;
    }

    //自定义sessionManager
    @Bean
    public SessionManager sessionManager(RedisSessionDAO redisSessionDAO) {
        MySessionManager mySessionManager = new MySessionManager();
        mySessionManager.setSessionDAO(redisSessionDAO);
        return mySessionManager;
    }

    /*如果redis集群，可以改为 RedisClusterManager*/
    @Bean
    @Profile("!prod")
    public IRedisManager redisManager() {
        log.warn("初始化了 redis 非集群 RedisManager");
        RedisManager redisManager = new RedisManager();
        redisManager.setHost(shiroRedisProperties.getRedisNodes()[0]);
        redisManager.setTimeout(1800);// 配置缓存过期时间
        redisManager.setDatabase(shiroRedisProperties.getDb());
        if (!Strings.isNullOrEmpty(shiroRedisProperties.getPassword()))
            redisManager.setPassword(shiroRedisProperties.getPassword());
        return redisManager;
    }
    @Bean
    @Profile("prod")
    public IRedisManager redisClusterManager(){
        log.warn("初始化了 redis 集群 RedisClusterManager");
        RedisClusterManager clusterManager = new RedisClusterManager();
        String redisNodes = StringUtils.join(shiroRedisProperties.getRedisNodes(), ",");
        clusterManager.setHost(redisNodes);
        if (!Strings.isNullOrEmpty(shiroRedisProperties.getPassword()))
            clusterManager.setPassword(shiroRedisProperties.getPassword());
        return clusterManager;
    }

    /**
     * sessionDao层的实现 通过redis
     */
    @Bean
    public RedisSessionDAO redisSessionDAO(IRedisManager redisManager) {
        RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
        redisSessionDAO.setRedisManager(redisManager);
        // redisSessionDAO.setSessionInMemoryTimeout(DEFAULT_GLOBAL_SESSION_TIMEOUT * 48);//设置shiro的session过期时间
        return redisSessionDAO;
    }

    /**
     * cacheManager 缓存 redis实现,使用的是shiro-redis开源插件
     */
    @Bean(name = "shiroCacheManager")
    public RedisCacheManager cacheManager(IRedisManager redisManager) {
        RedisCacheManager redisCacheManager = new RedisCacheManager();
        redisCacheManager.setRedisManager(redisManager);
        return redisCacheManager;
    }

    /**
     * 开启shiro aop注解支持.
     * 使用代理方式;所以需要开启代码支持;
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }
}
