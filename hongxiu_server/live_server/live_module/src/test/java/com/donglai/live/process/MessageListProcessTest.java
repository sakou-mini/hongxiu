package com.donglai.live.process;

import com.donglai.live.BaseTest;
import com.donglai.live.util.ComparatorUtil;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.support.ManagedList;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MessageListProcessTest extends BaseTest {

    @Test
    public void TestSortedMessageUserList() {
        List<MessageListProcess.MessageUserList> messageUserLists = new ManagedList<>();

        messageUserLists.add(new MessageListProcess.MessageUserList("1", true, 123));
        messageUserLists.add(new MessageListProcess.MessageUserList("2", false, 125));
        messageUserLists.add(new MessageListProcess.MessageUserList("3", false, 126));
        messageUserLists.add(new MessageListProcess.MessageUserList("4", false, 127));
        messageUserLists.add(new MessageListProcess.MessageUserList("5", true, 128));
        messageUserLists.add(new MessageListProcess.MessageUserList("6", true, 130));

        List<MessageListProcess.MessageUserList> sortedMessageList = messageUserLists.stream()
                .sorted(Comparator.comparing(MessageListProcess.MessageUserList::getPublishBlogsTime).reversed())
                .sorted(ComparatorUtil.getMessageUserListComparatorByIsLive()).collect(Collectors.toList());
        Assert.assertEquals("6", sortedMessageList.get(0).getUserId());
        Assert.assertEquals("2", sortedMessageList.get(sortedMessageList.size() - 1).getUserId());
    }
}
