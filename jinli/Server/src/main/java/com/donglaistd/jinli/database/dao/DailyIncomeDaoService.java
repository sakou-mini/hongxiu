package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.database.entity.rank.DailyIncome;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DailyIncomeDaoService {
    @Autowired
    private DailyIncomeRepository dailyIncomeRepository;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Transactional
    public DailyIncome insert(String userId, long amount) {
        DailyIncome income = new DailyIncome();
        income.setAmount(amount);
        income.setUserId(userId);
        income.setTime(System.currentTimeMillis());
        return dailyIncomeRepository.insert(income);
    }

    public List<DailyIncome> findByTimeBetween(int size, long startTime, long endTime) {
        Aggregation aggregation = Aggregation.newAggregation(DailyIncome.class,
                Aggregation.match(Criteria.where("time").gte(startTime).lte(endTime)),
                Aggregation.limit(size),
                Aggregation.group("$userId")
                        .first("userId").as("userId").sum("amount").as("amount"),
                Aggregation.project("userId", "amount"),
                Aggregation.sort(Sort.Direction.DESC, "amount")
        );
        AggregationResults<DailyIncome> incomes = mongoTemplate.aggregate(aggregation, DailyIncome.class, DailyIncome.class);
        return incomes.getMappedResults();
    }

    protected void deleteAll() {
        this.dailyIncomeRepository.deleteAll();
    }

    @Transactional
    public List<DailyIncome> saveAll(List<DailyIncome> list) {
        return dailyIncomeRepository.saveAll(list);
    }

    public void delete(DailyIncome dailyIncome) {
        dailyIncomeRepository.delete(dailyIncome);
    }

    public void deleteAll(List<DailyIncome> incomes) {
        dailyIncomeRepository.deleteAll(incomes);
    }
}
