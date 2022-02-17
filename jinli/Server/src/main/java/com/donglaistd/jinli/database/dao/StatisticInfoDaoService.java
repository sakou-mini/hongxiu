package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.statistic.StatisticInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class StatisticInfoDaoService {
    @Autowired
    private StatisticInfoRepository dailyBetInfoRepository;
    @Autowired
    private MongoTemplate mongoTemplate;

    public StatisticInfo save(StatisticInfo info) {
        return dailyBetInfoRepository.save(info);
    }

    public List<StatisticInfo> saveAll(List<StatisticInfo> infos) {
        return dailyBetInfoRepository.saveAll(infos);
    }

    public void delete(StatisticInfo info) {
        dailyBetInfoRepository.delete(info);
    }

    public void deleteAll(List<StatisticInfo> infos) {
        dailyBetInfoRepository.deleteAll(infos);
    }

    public List<StatisticInfo> findByLiveUserIdIsAndTypeIsOrderByTimeDesc(String  liveUserId, Constant.StatisticType type) {
        return dailyBetInfoRepository.findByLiveUserIdIsAndTypeIsOrderByTimeDesc(liveUserId, type).stream().limit(10).collect(Collectors.toList());
    }

    public List<StatisticInfo> findByLiveUserIdIsAndTypeIsOrderByTimeDesc(String liveUserId, Constant.StatisticType type, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "time");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<StatisticInfo> infos = dailyBetInfoRepository.findByLiveUserIdIsAndTypeIs(liveUserId, type, pageable);
        return infos.getContent();
    }
}
