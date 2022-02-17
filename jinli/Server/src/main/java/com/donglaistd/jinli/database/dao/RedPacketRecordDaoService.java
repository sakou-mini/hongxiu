package com.donglaistd.jinli.database.dao;


import com.donglaistd.jinli.database.entity.RedPacketRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;

@Service
public class RedPacketRecordDaoService {
    private static final Logger logger = Logger.getLogger(RedPacketRecordDaoService.class.getName());
    @Autowired
    RedPacketRecordRepository userRedPacketRepository;

    public RedPacketRecord save(RedPacketRecord userRedPacket) {
        return userRedPacketRepository.save(userRedPacket);
    }

    public List<RedPacketRecord> findByRedPacketId(String redPacketId) {
        return userRedPacketRepository.findByRedPacketId(redPacketId);
    }

    public List<RedPacketRecord> findByUserId(String userId) {
        return userRedPacketRepository.findByUserId(userId);
    }

    public RedPacketRecord findRedPacketRecordByUserIdAndRedPacketId(String userId,String redPacketId){
        return userRedPacketRepository.findByUserIdAndRedPacketId(userId, redPacketId);
    }
}
