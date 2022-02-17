package com.donglai.model.db.repository.account;

import com.donglai.model.db.entity.account.PrivateChatInBlack;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @author Moon
 * @date 2021-11-01 17:59
 */
public interface PrivateChatInBlackRepository extends MongoRepository<PrivateChatInBlack, Long> {

    /**
     * 查询这个用户下所有拉黑的人
     *
     * @param userId 需要查询的ID
     * @param del    是否删除
     * @return 数据集
     */
    List<PrivateChatInBlack> findByUserIdAndDelIs(String userId, boolean del);

    /**
     * 查询具体人员拉黑数据
     *
     * @param userId      拉黑人
     * @param blackUserId 被拉黑人
     * @return 数据
     */
    PrivateChatInBlack findByUserIdAndBlackUserId(String userId, String blackUserId);

    /**
     * 根据拉黑人和被拉黑人真实删除
     *
     * @param userId      拉黑人
     * @param blackUserId 被拉黑人
     */
    void deleteByUserIdAndBlackUserId(String userId, String blackUserId);
}
