package com.donglaistd.jinli.util;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.database.entity.Card;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;
import java.util.logging.Logger;

import static com.donglaistd.jinli.Constant.CardNumber.*;
import static com.donglaistd.jinli.Constant.CardType.*;

public class StringNumberUtilsTest extends BaseTest {

    Logger logger = Logger.getLogger(StringNumberUtilsTest.class.getName());

    @Test
    public void randomTest(){
        Set<String> ids = new HashSet<>();
        for (int i = 0; i < 514000; i++) {
            String id = null;
            id = StringNumberUtils.generateGameId(i,6);
            if(ids.contains(id)){
                logger.warning("repeated id:" +id);
                continue;
            }
            ids.add(id);
        }
        Assert.assertEquals(514000,ids.size());
    }
}
