package com.donglai.web.config.shiro;

import com.donglai.web.db.backoffice.entity.BackOfficeUser;
import com.donglai.web.db.backoffice.service.BackOfficeUserService;
import com.donglai.web.util.MD5Utils;
import lombok.extern.log4j.Log4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import javax.annotation.PostConstruct;

/**
 * @author yty
 * @version 1.0
 * @date 2020/4/11 16:33
 * @description: 自定义权限匹配和密码匹配器
 */
@Log4j
public class MyShiroRealm extends AuthorizingRealm {

    @Autowired
    @Lazy
    BackOfficeUserService backOfficeUserService;

    //权限认证
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        //权限验证
        log.debug("##################执行Shiro权限认证##################");
        BackOfficeUser backOfficeUser = (BackOfficeUser) principalCollection.getPrimaryPrincipal();
        if (backOfficeUser != null) {
            SimpleAuthorizationInfo smInfo = new SimpleAuthorizationInfo();
            smInfo.addRoles(backOfficeUser.getRoleList()); //角色存储
            //获取角色对应的权限
            smInfo.addStringPermissions(backOfficeUser.getRoleList());
            return smInfo;
        }
        return null;
    }

    //身份认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        log.info("用户验证执行 : " + token.getUsername());
        BackOfficeUser backOfficeUser = backOfficeUserService.findByUserName(token.getUsername());
        if (backOfficeUser == null) {
            log.error("用户 { " + token.getUsername() + " } 不存在 ");
            throw new AccountException("账户不存在");
        }
        //处理session(防止重复登录同一账号)
        /*SessionsSecurityManager securityManager = (SessionsSecurityManager) SecurityUtils.getSecurityManager();
        DefaultSessionManager sessionManager = (DefaultSessionManager) securityManager.getSessionManager();
        Collection<Session> sessions = sessionManager.getSessionDAO().getActiveSessions();//获取当前已登录的用户session列表
        for (Session session : sessions) {
            //清除该用户以前登录时保存的session, 如果和当前session是同一个session，则不剔除
            if (SecurityUtils.getSubject().getSession().getId().equals(session.getId()))
                continue;
            BackOfficeUser user = (BackOfficeUser) session.getAttribute("user");
            if (user != null) {
                if (token.getUsername().equals(user.getUsername())) {
                    log.info(token.getUsername() + "已登录，剔除中...===============");
                    sessionManager.getSessionDAO().delete(session);
                }
            }
        }*/
        //对密码进行加密
        String md5pwd = MD5Utils.setMd5Crytography(backOfficeUser.getUsername() + backOfficeUser.getPassword());
        backOfficeUser.setPassword(md5pwd);
        this.clearCachedAuthorizationInfo(SecurityUtils.getSubject().getPrincipals());
        return new SimpleAuthenticationInfo(backOfficeUser, backOfficeUser.getPassword(), getName());
    }

    @PostConstruct
    public void initCredentialsMatcher() {
        //该句作用是重写shiro的密码验证，让shiro用我自己的验证
        setCredentialsMatcher(new CredentialsMatcher());
    }

    //清空缓存
    public static void clearCache() {
        RealmSecurityManager rsm = (RealmSecurityManager) SecurityUtils.getSecurityManager();
        //AccountAuthorizationRealm为在项目中定义的realm类
        MyShiroRealm shiroRealm = (MyShiroRealm) rsm.getRealms().iterator().next();
        Subject subject = SecurityUtils.getSubject();
        String realmName = subject.getPrincipals().getRealmNames().iterator().next();
        Object principal = subject.getPrincipal();
        SimplePrincipalCollection principals = new SimplePrincipalCollection(subject.getPrincipal(), realmName);
        subject.runAs(principals);
        //用realm删除principle
        shiroRealm.getAuthorizationCache().remove(subject.getPrincipals());
        //切换身份也就是刷新了
        subject.releaseRunAs();

    }
}
