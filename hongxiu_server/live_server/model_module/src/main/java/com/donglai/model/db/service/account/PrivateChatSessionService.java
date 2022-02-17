package com.donglai.model.db.service.account;

import com.donglai.model.db.entity.account.PrivateChatSession;
import com.donglai.model.db.repository.account.PrivateChatSessionRepository;
import com.donglai.protocol.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author Moon
 * @date 2021-11-01 14:56
 */
@Service
public class PrivateChatSessionService {

    @Autowired
    private PrivateChatSessionRepository repository;

    public long addPrivateChatSession(String fromUid, Account.AccountOfPrivateChatRequest request) {

        PrivateChatSession session = repository.findByFromUidAndToUid(fromUid, request.getToUid());
        //如果为空则添加会话
        if (Objects.isNull(session)) {
            session = repository.save(PrivateChatSession.newInstance(request.getMessage(), System.currentTimeMillis(), fromUid, request.getToUid(), false));
        } else {
            session.setLastTime(System.currentTimeMillis());
            session.setLastMessage(request.getMessage());
            session.setDel(false);
            repository.save(session);
        }
        return session.getId();
    }

    public PrivateChatSession findById(long sessionId) {
        return repository.findById(sessionId).orElse(null);
    }

    public PrivateChatSession save(PrivateChatSession privateChatSession) {
        return repository.save(privateChatSession);
    }

    /**
     * 根据发送者和接受者查询会话
     *
     * @param fromUid 发送者
     * @param toUid   接受者
     * @return 会话对象
     */
    public PrivateChatSession findByFromUidAndToUid(String fromUid, String toUid) {
        return repository.findByFromUidAndToUid(fromUid, toUid);
    }

    /**
     * 查询自己的所有发送列表
     *
     * @param userId 发送者
     * @param del    是否删除
     * @return 数据集
     */
    public List<PrivateChatSession> findByFromUidAndDelIsAndToUidInOrderByLastTimeDesc(String userId, boolean del, List<String> userIds) {
        return repository.findByFromUidAndDelIsAndToUidInOrderByLastTimeDesc(userId, del, userIds);
    }

    public List<PrivateChatSession> findByFromUidAndToUidInAndDelIsOrderByLastTimeDesc(String fromUid, List<String> toUids, boolean del) {
        return repository.findByFromUidAndToUidInAndDelIsOrderByLastTimeDesc(fromUid, toUids, del);

    }

    public List<PrivateChatSession> findByFromUidAndDelIs(String userId, boolean isDel) {
        return repository.findByFromUidAndDelIs(userId, isDel);
    }

    public List<PrivateChatSession> findByFromUid(String userId) {
        return repository.findByFromUid(userId);
    }
}
