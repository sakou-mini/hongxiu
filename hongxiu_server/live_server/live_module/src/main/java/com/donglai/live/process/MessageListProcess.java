package com.donglai.live.process;

import com.donglai.live.util.ComparatorUtil;
import com.donglai.model.db.entity.common.statistics.UserOperationRecord;
import com.donglai.model.db.service.common.statistics.UserOperationRecordService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class MessageListProcess {
    @Autowired
    UserProcess userProcess;
    @Autowired
    UserOperationRecordService userOperationRecordService;

    //sort by publishBlogsTime and liveStatus
    public List<MessageUserList> sortedUserLeaderList(List<String> leaderIds) {
        Map<String, MessageUserList> messageUserListMap = leaderIds.stream().map(id -> new MessageUserList(id, userProcess.userIsOpenLive(id)))
                .collect(Collectors.toMap(MessageUserList::getUserId, userList -> userList));
        List<UserOperationRecord> publishBlogsRecord = userOperationRecordService.findUserOperationRecordOrderByPublishBlogsTime(leaderIds);
        publishBlogsRecord.forEach(record -> messageUserListMap.get(record.getUserId()).setPublishBlogsTime(record.getLastPublishBlogsTime()));
        return messageUserListMap.values().stream()
                .sorted(Comparator.comparing(MessageUserList::getPublishBlogsTime).reversed())
                .sorted(ComparatorUtil.getMessageUserListComparatorByIsLive()).collect(Collectors.toList());
    }

    @Data
    @AllArgsConstructor
    public static class MessageUserList {
        private String userId;
        private boolean isLive;
        private long publishBlogsTime;

        public MessageUserList(String userId, boolean isLive) {
            this.userId = userId;
            this.isLive = isLive;
        }
    }
}
