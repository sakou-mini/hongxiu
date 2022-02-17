package com.donglaistd.jinli.builder;

import com.donglaistd.jinli.BaseTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TaskBuilderTest extends BaseTest {
    @Autowired
    TaskBuilder taskBuilder;

    @Test
    public void TaskBuilderShouldGenerateTaskConfig() {
        Assert.assertEquals(5, taskBuilder.getDailyTaskConfigMap().size());
        Assert.assertEquals(7, taskBuilder.getSignInTaskConfigMap().size());
        Assert.assertEquals(12, taskBuilder.getPermanentTaskConfigMap().size());
    }
}
