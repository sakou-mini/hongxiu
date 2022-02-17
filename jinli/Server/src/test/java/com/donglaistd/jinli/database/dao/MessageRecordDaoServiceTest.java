package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.database.entity.chat.MessageRecord;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class MessageRecordDaoServiceTest extends BaseTest {
    @Autowired
    MessageRecordDaoService messageRecordDaoService;

    @Test
    public void createMessageRecordTest(){
        MessageRecord messageRecord = MessageRecord.newInstance("hi", "123", "321");
        messageRecordDaoService.saveMessage(messageRecord);
        long recordNum = messageRecordDaoService.countChatHistoryBySenderAndReceiver("123", "321");
        Assert.assertEquals(1,recordNum);
        recordNum = messageRecordDaoService.countChatHistoryBySenderAndReceiver("321","123");
        Assert.assertEquals(1,recordNum);
    }
}
