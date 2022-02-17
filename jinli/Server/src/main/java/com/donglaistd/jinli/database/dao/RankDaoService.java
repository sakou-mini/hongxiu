package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.rank.Rank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RankDaoService {
    @Autowired
    private RankRepository rankRepository;
    @Transactional
    public Rank save(Rank rank) {
        return rankRepository.save(rank);
    }

    @Transactional
    public List<Rank> saveAll(List<Rank> ranks) {
        return rankRepository.saveAll(ranks);
    }

    public List<Rank> findAllByRankTypeIsAndTimeBetween(Constant.RankType rankType,long startTime, long endTime) {
       return rankRepository.findAllByRankTypeIsAndTimeBetween(rankType, startTime, endTime);
    }

    public void deleteAll(List<Rank> ranks) {
        rankRepository.deleteAll(ranks);
    }
}
