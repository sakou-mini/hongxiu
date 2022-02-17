package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.database.entity.zone.DiaryStar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.donglaistd.jinli.constant.CacheNameConstant.DiaryStars;

@Service
public class DiaryStarDaoService {

    @Autowired
    DiaryStarRepository diaryStarRepository;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    private String getCacheKeyName(String diaryId) {
        return DiaryStars + "_" + diaryId;
    }

    public DiaryStar findByDiaryIdAndUserId(String diaryId, String userId){
        return diaryStarRepository.findByDiaryIdAndUserId(diaryId, userId);
    }

    public DiaryStar save(DiaryStar diaryStar){
        redisTemplate.delete(getCacheKeyName(diaryStar.getDiaryId()));
        return diaryStarRepository.save(diaryStar);
    }

    public long countDiaryStarNum(String diaryId){
        String cacheKeyName = getCacheKeyName(diaryId);
        Integer count = (Integer) redisTemplate.opsForValue().get(cacheKeyName);
        if(Objects.isNull(count)){
            count = diaryStarRepository.countByDiaryId(diaryId);
            redisTemplate.opsForValue().set(cacheKeyName,count);
        }
        return count;
    }

    public void delete(DiaryStar diaryStar) {
        redisTemplate.delete(getCacheKeyName(diaryStar.getDiaryId()));
        diaryStarRepository.delete(diaryStar);
    }

    public boolean isStar(String diaryId, String userId){
        return Objects.nonNull(findByDiaryIdAndUserId(diaryId,userId));
    }

    public void deleteDiaryStarByDiaryId (String diaryId){
        redisTemplate.delete(getCacheKeyName(diaryId));
        diaryStarRepository.deleteAllByDiaryId(diaryId);
    }

}
