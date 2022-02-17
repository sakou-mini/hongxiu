package com.donglai.model.db.service.account;

import com.donglai.model.db.entity.account.PrivateChatSession_Message;
import com.donglai.model.db.repository.account.PrivateChatSessionMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Moon
 * @date 2021-11-02 11:40
 */
@Service
public class PrivateChatSessionMessageService {

    @Autowired
    private PrivateChatSessionMessageRepository repository;

    /**
     * 根据会话ID 删除所有聊天记录关联
     *
     * @param sessionId 会话ID
     */
    public void delMessage(long sessionId) {
        repository.deleteBySessionId(sessionId);
    }

    /**
     * 根据会话ID查询所有的聊天记录关联
     *
     * @param sessionId 会话ID
     * @return 返回数据集
     */
    public List<PrivateChatSession_Message> findBySessionId(long sessionId) {
        return repository.findBySessionId(sessionId);
    }

    public PrivateChatSession_Message save(PrivateChatSession_Message privateChatSession_message) {
        return repository.save(privateChatSession_message);
    }
}
