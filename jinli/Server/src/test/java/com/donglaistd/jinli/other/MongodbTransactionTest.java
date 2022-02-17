package com.donglaistd.jinli.other;


import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.entity.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.MongoTransactionException;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.logging.Logger;

import static org.junit.Assert.fail;


@RunWith(SpringRunner.class)
@SpringBootTest
public class MongodbTransactionTest extends BaseTest {

    private static final Logger logger = Logger.getLogger(MongodbTransactionTest.class.getName());

    @Autowired
    private UserDaoService userDaoService;

    @Autowired
    TransactionTemplate transactionTemplate;

    @Override
    @Before
    public void setUp() {
    }

    @Override
    @After
    public void tearDown() {
    }

    @Test
    @Transactional
    public void TestTransactionRollback() {
        String name = "trans_tester1";
        try {
            User user1 = createTester(100, name);
            User user2 = createTester(100, name);
        } catch (DuplicateKeyException e) {
            try {
                userDaoService.findByAccountName(name);
                fail("test failed");
            } catch (MongoTransactionException exception) {
                logger.info("catches TransactionException");
            }
        }
    }

    protected User createTester(int coinCount, String name) {
        var user = userBuilder.createUser(name, name, name);
        user.setGameCoin(coinCount);
        return user;
    }
}
