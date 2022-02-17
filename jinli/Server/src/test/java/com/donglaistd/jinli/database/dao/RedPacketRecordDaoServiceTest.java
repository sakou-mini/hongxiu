package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.builder.RedPacketBuilder;
import com.donglaistd.jinli.database.entity.RedPacket;
import com.donglaistd.jinli.database.entity.RedPacketRecord;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.processors.handler.redpacket.GrabRedPacketRequestHandler;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class RedPacketRecordDaoServiceTest extends BaseTest {
    private static final Logger logger = Logger.getLogger(RedPacketRecordDaoServiceTest.class.getName());
    @Autowired
    RedPacketRecordDaoService redPacketRecordDaoService;
    @Autowired
    RedPacketBuilder redPacketBuilder;
    @Autowired
    GrabRedPacketRequestHandler grabRedPacketRequestHandler;


    public ExecutorService createThreadPool()
    {
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        return new ThreadPoolExecutor(availableProcessors, availableProcessors, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>());
    }

    @Test
    public void getRedPacketForSingleThreadTest(){
        RedPacket redPacket = redPacketBuilder.create("k120",100, 9);
        logger.info("奖金为："+redPacket.getRandomCoinList());
        Jinli.JinliMessageReply reply;
        User tester;
        for (int i = 0; i < 90; i++) {
            tester = createTester(100, "name:" + i);
            dataManager.saveUser(tester);
            reply = grabRedPacketRequestHandler.dealOpenGetRedPacket(tester, redPacket);
            if(!reply.getResultCode().equals(Constant.ResultCode.SUCCESS))
                logger.info("未领取到红包 for:------------>"+i);
            else
                logger.info("领取成功for:------------>uid:"+i+"  coin:-->"+reply.getGrabRedPacketReply().getCoin());

        }
        List<RedPacketRecord> records = redPacketRecordDaoService.findByRedPacketId(redPacket.getId());
        Assert.assertEquals(9,records.size());
        double sum = records.stream().mapToDouble(RedPacketRecord::getAmount).sum();
        Assert.assertEquals(100,sum,0);
    }


    @Test
    public void getRedPacketForMultiThreadTest(){
        ExecutorService executorService = createThreadPool();
        int peopleNum = 100;
        int redPacketCoin = 30000;
        int redPacketNum = 100;
        RedPacket redPacket = redPacketBuilder.create("k120",redPacketCoin, redPacketNum);
        long start = System.currentTimeMillis();
        User tester;
        for (int i = 0; i < peopleNum; i++) {
            tester = createTester(100, "name:" + i);
            User finalTester = tester;
            executorService.submit(() -> {
                grabRedPacketRequestHandler.dealOpenGetRedPacket(finalTester, redPacket);
            });
        }
        executorService.shutdown();
        while (true){
            if(executorService.isTerminated()){
                long end = System.currentTimeMillis();
                List<RedPacketRecord> records = redPacketRecordDaoService.findByRedPacketId(redPacket.getId());
                int sum = records.stream().mapToInt(RedPacketRecord::getAmount).sum();
                logger.info("grab num ："+records.size()+"--->redPacket used coin ："+sum+"---left coin："+(redPacket.getAmountCoin() - sum)+"========》total spend time（mill）:"+ (end - start));
                break;
            }
        }
    }
}
