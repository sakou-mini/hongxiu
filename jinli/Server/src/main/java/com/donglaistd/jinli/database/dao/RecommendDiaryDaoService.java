package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.database.entity.zone.RecommendDiary;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecommendDiaryDaoService {
    final RecommendDiaryRepository recommendDiaryRepository;

    public RecommendDiaryDaoService(RecommendDiaryRepository recommendDiaryRepository) {
        this.recommendDiaryRepository = recommendDiaryRepository;
    }

    public List<RecommendDiary> findAll(){
        return recommendDiaryRepository.findAll();
    }

    public void deleteById(String diaryId) {
        recommendDiaryRepository.deleteById(diaryId);
    }

    public void deleteRecommendDiary(RecommendDiary recommendDiary) {
        recommendDiaryRepository.delete(recommendDiary);
    }

    public int deleteNotRecommendDiary(){
        return recommendDiaryRepository.deleteAllByEndTimeLessThanEqual(System.currentTimeMillis());
    }

    public RecommendDiary save(RecommendDiary recommendDiary){
        return recommendDiaryRepository.save(recommendDiary);
    }

    public RecommendDiary findById(String id){
        return recommendDiaryRepository.findById(id).orElse(null);
    }
}
