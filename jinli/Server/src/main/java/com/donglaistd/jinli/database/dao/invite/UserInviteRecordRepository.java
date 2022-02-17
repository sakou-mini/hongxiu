package com.donglaistd.jinli.database.dao.invite;

import com.donglaistd.jinli.database.entity.invite.UserInviteRecord;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserInviteRecordRepository extends MongoRepository<UserInviteRecord, String> {
    UserInviteRecord findByBeInviteUserId(String beInviteUserId);

    List<UserInviteRecord> findAllByInviteUserId(String inviteUserId);
}
