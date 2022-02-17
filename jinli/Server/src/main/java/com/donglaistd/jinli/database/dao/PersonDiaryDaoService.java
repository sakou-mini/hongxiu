package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.zone.PersonDiary;
import com.donglaistd.jinli.database.entity.zone.RecommendDiary;
import com.donglaistd.jinli.http.dto.request.DiaryListRequest;
import com.google.common.collect.Lists;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.donglaistd.jinli.Constant.DiaryStatue.DIARY_UPLOADING;
import static com.donglaistd.jinli.constant.CacheNameConstant.UserDiary;

@Service
public class PersonDiaryDaoService {
    @Autowired
    PersonDiaryRepository diaryRepository;

    @Autowired
    DiaryReplyDaoService diaryReplyDaoService;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;
    @Autowired
    DiaryResourceDaoService diaryResourceDaoService;
    @Autowired
    DiaryStarDaoService diaryStarDaoService;
    @Autowired
    RecommendDiaryDaoService recommendDiaryDaoService;
    private static final long OVERDUE_MILLTIME=6000;

    private String getDiaryCacheKeyName(String diaryId) {
        return UserDiary + "_" + diaryId;
    }

    public PersonDiary findById(String id) {
        PersonDiary diary = (PersonDiary) redisTemplate.opsForValue().get(getDiaryCacheKeyName(id));
        return Objects.isNull(diary) ? diaryRepository.findById(id) : diary;
    }

    public PersonDiary save(PersonDiary personDiary) {
        personDiary = diaryRepository.save(personDiary);
        redisTemplate.opsForValue().set(getDiaryCacheKeyName(personDiary.getId()), personDiary,OVERDUE_MILLTIME, TimeUnit.MILLISECONDS);
        return personDiary;
    }

    @Transactional
    public void deleteAndCleanDiaryCache(PersonDiary personDiary) {
        redisTemplate.delete(getDiaryCacheKeyName(personDiary.getId()));
        diaryRepository.delete(personDiary);
        diaryResourceDaoService.deleteByDiaryId(personDiary.getId());
        diaryStarDaoService.deleteDiaryStarByDiaryId(personDiary.getId());
        recommendDiaryDaoService.deleteById(personDiary.getId());
    }

    public List<PersonDiary> findAllByIds(Collection<String> ids) {
        return diaryRepository.findAllByIdIn(ids);
    }

    public List<String> findAllApprovalPassIdsLimit(int num) {
        Query query = new Query(Criteria.where("statue").is(Constant.DiaryStatue.DIARY_APPROVAL_PASS)).with(Sort.by(Sort.Direction.DESC, "uploadTime","playNumber")).limit(num);
        query.fields().include("id");
        List<PersonDiary> diaryList = mongoTemplate.find(query, PersonDiary.class);
        return diaryList.stream().map(PersonDiary::getId).collect(Collectors.toList());
    }

    public PageImpl<PersonDiary> findAllUnapprovedByTimeRangeAndUserId(String userId, Long startTime, Long endTime, Constant.DiaryStatue statue, int page, int size) {
        Criteria criteria = Criteria.where("statue").is(statue);
        Pageable thePage = PageRequest.of(page, size);
        List<Criteria> criteriaList = new ArrayList<>();
        if (Strings.isNotBlank(userId)) criteriaList.add(Criteria.where("userId").regex(userId + ".*"));
        if (Objects.nonNull(startTime)) criteriaList.add(Criteria.where("uploadTime").gte(startTime));
        if (Objects.nonNull(endTime)) criteriaList.add(Criteria.where("uploadTime").lte(endTime));
        if (!criteriaList.isEmpty()) criteria = criteria.andOperator(criteriaList.toArray(new Criteria[0]));
        Query query = Query.query(criteria);
        long count = mongoTemplate.count(query, PersonDiary.class);
        return new PageImpl<>(mongoTemplate.find(query.with(thePage), PersonDiary.class), PageRequest.of(page, size), count);
    }

    public long countUserNotFailDiaryNum(String userId){
        return diaryRepository.countByUserIdAndStatueIsNot(userId,Constant.DiaryStatue.DIARY_APPROVED_FAIL);
    }

    public List<PersonDiary> findUserNotFailDiary(String userId){
        return diaryRepository.findAllByUserIdAndStatueNotIn(userId, Lists.newArrayList(Constant.DiaryStatue.DIARY_APPROVED_FAIL, DIARY_UPLOADING));
    }

    public List<PersonDiary> findDiaryByUserIdAndStatue(String userId,Constant.DiaryStatue statue){
        return diaryRepository.findByUserIdAndStatueEquals(userId, statue);
    }

    public List<PersonDiary> findDiaryByStatue(Constant.DiaryStatue statue){
        return diaryRepository.findAllByStatue(statue);
    }

    public void saveAll(List<PersonDiary> diaries) {
        List<PersonDiary> personDiaries = diaryRepository.saveAll(diaries);
        for (PersonDiary diary : personDiaries) {
            redisTemplate.opsForValue().set(getDiaryCacheKeyName(diary.getId()), diary);
        }
    }

    public PageImpl<PersonDiary> findByStatueAndRecommendAndTimeRangeAndUserId(String userId,Boolean recommend, Long startTime, Long endTime, Constant.DiaryStatue statue,PageRequest pageInfo) {
        Criteria criteria = Criteria.where("statue").is(statue);
        if(recommend!=null){
            Object[] ids = recommendDiaryDaoService.findAll().stream().map(RecommendDiary::getDiaryId).toArray();
           if(recommend) criteria.and("_id").in(ids);
           else criteria.and("_id").nin(ids);
        }
        List<Criteria> criteriaList = new ArrayList<>();
        if (Strings.isNotBlank(userId)) criteriaList.add(Criteria.where("userId").regex(userId + ".*"));
        if (Objects.nonNull(startTime)) criteriaList.add(Criteria.where("uploadTime").gte(startTime));
        if (Objects.nonNull(endTime)) criteriaList.add(Criteria.where("uploadTime").lte(endTime));
        if (!criteriaList.isEmpty()) criteria.andOperator(criteriaList.toArray(new Criteria[0]));
        Query query = Query.query(criteria);
        long count = mongoTemplate.count(query, PersonDiary.class);
        return new PageImpl<>(mongoTemplate.find(query.with(pageInfo), PersonDiary.class), pageInfo, count);
    }

    public PageImpl<PersonDiary> findDiaryListByRequest(DiaryListRequest request) {
        List<Criteria> criteriaList = new ArrayList<>();
        if(request.getStatueType()!=null && !Objects.equals(request.getStatueType(),Constant.DiaryStatue.Diary_NOSET)){
            criteriaList.add(Criteria.where("statue").is(request.getStatueType()));
        }
        if (Strings.isNotBlank(request.getUserId())) criteriaList.add(Criteria.where("userId").regex(request.getUserId() + ".*"));
        if (Objects.nonNull(request.getStartTime())) criteriaList.add(Criteria.where("uploadTime").gte(request.getStartTime()));
        if (Objects.nonNull(request.getEndTime())) criteriaList.add(Criteria.where("uploadTime").lte(request.getEndTime()));
        if(Objects.nonNull(request.getRecommendType())){
            Object[] ids = recommendDiaryDaoService.findAll().stream().map(RecommendDiary::getDiaryId).toArray();
            if(request.getRecommendType()) criteriaList.add(Criteria.where("id").in(ids));
            else criteriaList.add(Criteria.where("id").nin(ids));
        }
        Criteria criteria = new Criteria();
        if (!criteriaList.isEmpty()) criteria.andOperator(criteriaList.toArray(new Criteria[0]));
        Query query = Query.query(criteria);
        long count = mongoTemplate.count(query, PersonDiary.class);
        PageRequest pageInfo = PageRequest.of(request.getPage(), request.getSize());
        return new PageImpl<>(mongoTemplate.find(query.with(pageInfo).with(Sort.by(Sort.Direction.DESC,"uploadTime")), PersonDiary.class), pageInfo, count);
    }
}
