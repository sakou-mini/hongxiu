package com.donglai.web.process;

import com.donglai.web.db.backoffice.entity.BackOfficeUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class ShiroProcess {
  /*  @Autowired
    SessionDAO sessionDAO;*/

    public void logoutByBackUsers(List<BackOfficeUser> backOfficeUsers){
     /*   List<String> usernames = backOfficeUsers.stream().map(BackOfficeUser::getUsername).collect(Collectors.toList());
        SessionsSecurityManager securityManager = (SessionsSecurityManager) SecurityUtils.getSecurityManager();
        DefaultSessionManager sessionManager = (DefaultSessionManager) securityManager.getSessionManager();
        SessionDAO sessionDAO = sessionManager.getSessionDAO();
        Collection<Session> sessions = sessionDAO.getActiveSessions();//
        String username;
        for (Session session : sessions) {
            username = (String) session.getAttribute(SESSION_USERNAME);
            if(Objects.nonNull(username) && usernames.contains(username)) {
                log.info(usernames + "剔除中...===============");
                sessionManager.getSessionDAO().delete(session);
            }
        }*/
    }
}
