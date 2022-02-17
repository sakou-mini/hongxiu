package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.database.entity.rank.DailyIncome;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DailyIncomeRepository extends MongoRepository<DailyIncome,String> {
    List<DailyIncome> findAll();

    Page<DailyIncome> findAll(Example example, Pageable pageable);
}
