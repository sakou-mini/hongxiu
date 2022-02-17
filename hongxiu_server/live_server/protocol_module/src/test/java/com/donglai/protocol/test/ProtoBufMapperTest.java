package com.donglai.protocol.test;

import com.donglai.protocol.App;
import com.donglai.protocol.ProtoBufMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
public class ProtoBufMapperTest {

    @Test
    public void test() {
        Map<ProtoBufMapper.MessageType, Map<Integer, String>> opCodeTopic = ProtoBufMapper.getOpCodeTopic();


    }
}
