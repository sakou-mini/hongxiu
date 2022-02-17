package com.donglai.model.db.service.account;

import com.donglai.model.db.entity.account.PrivateChatInBlack;
import com.donglai.model.db.repository.account.PrivateChatInBlackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Moon
 * @date 2021-11-01 18:07
 */
@Service
public class PrivateChatInBlackService {
    @Autowired
    private PrivateChatInBlackRepository privateChatInBlackRepository;

    /**
     * 查询这个用户下所有拉黑的人
     *
     * @param userId 需要查询的ID
     * @param del    是否删除
     * @return 数据集
     */
    public List<PrivateChatInBlack> findBlackMenuByUserId(String userId, boolean del) {
        return privateChatInBlackRepository.findByUserIdAndDelIs(userId, del);
    }

    /**
     * 查询具体人员拉黑数据
     *
     * @param userId      拉黑人
     * @param blackUserId 被拉黑人
     * @return 数据
     */
    public PrivateChatInBlack findByUserIdAndBlackUserId(String userId, String blackUserId) {
        return privateChatInBlackRepository.findByUserIdAndBlackUserId(userId, blackUserId);
    }

    /**
     * 根据拉黑人和被拉黑人真实删除
     *
     * @param userId      拉黑人
     * @param blackUserId 被拉黑人
     */
    public void deleteByUserIdAndBlackUserId(String userId, String blackUserId) {
        privateChatInBlackRepository.deleteByUserIdAndBlackUserId(userId, blackUserId);
    }

    public PrivateChatInBlack save(PrivateChatInBlack privateChatInBlack) {
        return privateChatInBlackRepository.save(privateChatInBlack);
    }
}
