package com.donglai.model.db.service.account;

import com.donglai.model.db.entity.account.PrivateChat;
import com.donglai.model.db.repository.account.PrivateChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PrivateChatService {
    @Autowired
    PrivateChatRepository repository;


    /**
     * 入库消息
     *
     * @param privateChat 消息对象
     * @return 返回
     */
    public PrivateChat save(PrivateChat privateChat) {
        return repository.save(privateChat);
    }

    /**
     * 根据ID集合查询所有聊天数据
     *
     * @param ids 聊天记录ID
     * @return 返回数据集
     */
    public List<PrivateChat> findByIdInOrderByTimeAsc(List<Long> ids) {
        return repository.findByIdInOrderByTimeAsc(ids);
    }

    public List<PrivateChat> getUnreadBySessionIdAndToUid(String userId, List<Long> messageIds) {
        return repository.findByIdInAndToUidIs(messageIds, userId);
    }

    public void deleteAll(List<PrivateChat> privateChat) {
        repository.deleteAll(privateChat);
    }
}
