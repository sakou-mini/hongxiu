package com.donglaistd.jinli.service;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.*;
import com.donglaistd.jinli.database.entity.zone.DiaryResource;
import com.donglaistd.jinli.database.entity.zone.PersonDiary;
import com.donglaistd.jinli.database.entity.zone.RecommendDiary;
import com.donglaistd.jinli.http.dto.request.DiaryListRequest;
import com.donglaistd.jinli.http.entity.PageInfo;
import com.donglaistd.jinli.http.entity.PersonDiaryListSend;
import com.donglaistd.jinli.http.entity.RecommendDiaryDetail;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.RandomUtil;
import com.donglaistd.jinli.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.donglaistd.jinli.constant.CacheNameConstant.getDiaryViewHistoryKey;

@Component
public class DiaryProcess {
    private static final Logger logger = Logger.getLogger(DiaryProcess.class.getName());
    @Autowired
    DiaryResourceDaoService diaryResourceDaoService;
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    DataManager dataManager;
    @Autowired
    RecommendDiaryDaoService recommendDiaryDaoService;
    @Autowired
    PersonDiaryDaoService personDiaryDaoService;
    @Autowired
    UserDaoService userDaoService;
    @Value("${max.recommend.num}")
    private int maxRecommendNum;
    @Autowired
    private PersonDiaryOperationLogDaoService diaryOperationLogDaoService;
    @Autowired
    private BackOfficeUserDaoService backOfficeUserDaoService;

    private void loadDiaryResourceAndThumbnailUrl(Jinli.Diary.Builder builder){
        List<DiaryResource> resource = diaryResourceDaoService.findAllResourceByDiaryId(builder.getId());
        if(StringUtils.isNullOrBlank(builder.getThumbnailUrl()) && !resource.isEmpty()){
            builder.setThumbnailUrl(resource.get(0).getResourceUrl());
        }
        List<String> resourceUrl = resource.stream().map(DiaryResource::getResourceUrl).collect(Collectors.toList());
        builder.addAllResourcePath(resourceUrl);
    }

    public Jinli.Diary getDiarySummaryProto(PersonDiary diary){
        Jinli.Diary.Builder builder = diary.toSummaryProto().toBuilder();
        loadDiaryResourceAndThumbnailUrl(builder);
        return builder.build();
    }

    public Jinli.Diary getDiaryProto(PersonDiary diary){
        Jinli.Diary.Builder builder = diary.toProto().toBuilder();
        loadDiaryResourceAndThumbnailUrl(builder);
        return builder.build();
    }

    public void removeUserDiaryHistory(String userId){
        String key = getDiaryViewHistoryKey(userId);
        logger.fine("clean view diary history" );
        redisTemplate.delete(key);
    }

    public Set<String> randomDiary(String userId,int randomNum){
        String key = getDiaryViewHistoryKey(userId);
        Set<String> diaryIds = new LinkedHashSet<>();
        Set<String> viewHistory = redisTemplate.opsForSet().members(key);
        List<String> hotDiaries = recommendDiaryDaoService.findAll().stream().sorted(Comparator.comparing(RecommendDiary::getPosition))
                .map(RecommendDiary::getDiaryId).filter(id->!viewHistory.contains(id)).collect(Collectors.toList());
        //1.add recommend Diary if not view
        for (int i = 0; i < hotDiaries.size() && i < randomNum; i++) {
            diaryIds.add(hotDiaries.get(i));
        }
        viewHistory.addAll(diaryIds);
        if(diaryIds.size() >= randomNum) {
            addDiaryViewHistory(userId, viewHistory.toArray(new String[0]));
            return diaryIds;
        }
        //2.randomOther not view Diary
        HashSet<String> passDiaryIds = new HashSet<>(dataManager.getPassDiaryIds());
        passDiaryIds.removeAll(viewHistory);
        diaryIds.addAll(RandomUtil.randomIds(passDiaryIds, (randomNum - diaryIds.size())));
        viewHistory.addAll(diaryIds);
        addDiaryViewHistory(userId, viewHistory.toArray(new String[0]));
        if(viewHistory.size() >= dataManager.getPassDiaryIds().size()) {
            removeUserDiaryHistory(userId);
        }
        return diaryIds;
    }

    public void addDiaryViewHistory(String userId,String... diaryIds){
        if(diaryIds.length>0)
            redisTemplate.opsForSet().add(getDiaryViewHistoryKey(userId), diaryIds);
    }

    //  query approvedDiaryList
    public PageInfo<PersonDiaryListSend> getApprovedDiaryList(String useId, Long startTime, Long endTime , int recommendStatue, PageRequest pageRequest){
        Boolean recommend = null;
        if(recommendStatue>0) recommend = recommendStatue == 1;
        List<PersonDiaryListSend> diaryListSend = new ArrayList<>();
        PageImpl<PersonDiary> result = personDiaryDaoService.findByStatueAndRecommendAndTimeRangeAndUserId(useId, recommend, startTime, endTime,
                Constant.DiaryStatue.DIARY_APPROVAL_PASS, pageRequest);
        List<PersonDiary> content = result.getContent();
        for (PersonDiary personDiary : content) {
            diaryListSend.add(buildPersonDiaryListSendByPersonDiary(personDiary,Objects.nonNull(recommendDiaryDaoService.findById(personDiary.getId()))));
        }
        return new PageInfo<>(diaryListSend, result.getTotalElements());
    }

    private PersonDiaryListSend buildPersonDiaryListSendByPersonDiary(PersonDiary personDiary, boolean recommend){
        PersonDiaryListSend diaryListSend = new PersonDiaryListSend();
        diaryListSend.content = personDiary.getContent();
        diaryListSend.displayName = userDaoService.findById(personDiary.getUserId()).getDisplayName();
        diaryListSend.id = personDiary.getId();
        diaryListSend.resourceUrlLis = diaryResourceDaoService.findDiaryResource(personDiary.getId());
        diaryListSend.state = personDiary.getStatue();
        diaryListSend.type = personDiary.getType();
        diaryListSend.uploadTime = personDiary.getUploadTime();
        diaryListSend.operationDate = new Date(personDiary.getUploadTime());
        diaryListSend.userId = personDiary.getUserId();
        diaryListSend.recommend = recommend;
        var logMsg = diaryOperationLogDaoService.findByPersonDiaryId(personDiary.getId());
        if(logMsg != null){
            diaryListSend.operationDate = logMsg.getOperationDate();
            var backOfficeMsg = backOfficeUserDaoService.findById(logMsg.getBackOfficeId());
            if(Objects.nonNull(backOfficeMsg))
                diaryListSend.backOfficeName = backOfficeMsg.getAccountName();
        }
        return diaryListSend;
    }

    //recommendDetail
    public RecommendDiaryDetail getRecommendDiaryDetail(String id) {
        PersonDiary diary = personDiaryDaoService.findById(id);
        if (diary == null) return null;
        RecommendDiary recommendDiary = recommendDiaryDaoService.findById(id);
        RecommendDiaryDetail recommendDetail = new RecommendDiaryDetail();
        recommendDetail.id = diary.getId();
        if (recommendDiary != null) {
            recommendDetail.recommend = recommendDiary.isRecommend();
            recommendDetail.recommendTime = recommendDiary.getRecommendTime();
            recommendDetail.position = recommendDiary.getPosition();
        }
        recommendDetail.unavailablePositions = recommendDiaryDaoService.findAll().stream().map(RecommendDiary::getPosition).collect(Collectors.toList());
        recommendDetail.recommendSize = recommendDetail.unavailablePositions.size();
        return recommendDetail;
    }

    //recommendDiary
    public Constant.ResultCode recommendDiary(String id, Constant.DiaryRecommendTimeType recommendTime, int position){
        PersonDiary diary = personDiaryDaoService.findById(id);
        if(Objects.isNull(diary) || !Objects.equals(diary.getStatue(),Constant.DiaryStatue.DIARY_APPROVAL_PASS))
            return Constant.ResultCode.DIARY_NOTPASS;
        List<RecommendDiary> recommendDiaries = recommendDiaryDaoService.findAll();
        if(recommendDiaries.size() >= maxRecommendNum) return Constant.ResultCode.OVER_MAX_RECOMMEND_NUM;
        if(recommendDiaries.stream().anyMatch(recommendDiary -> Objects.equals(recommendDiary.getDiaryId(),id)))
            return Constant.ResultCode.RECOMMEND_POSITION_USED;
        RecommendDiary recommendDiary = RecommendDiary.newInstance(id, position, recommendTime);
        try {
            recommendDiaryDaoService.save(recommendDiary);
        } catch (Exception e) {
            e.printStackTrace();
            return Constant.ResultCode.RECOMMEND_POSITION_USED;
        }
        return Constant.ResultCode.SUCCESS;
    }

    public void cancelRecommendDiary(String id) {
        recommendDiaryDaoService.deleteById(id);
    }

    public PageInfo<PersonDiaryListSend> findDiaryList(DiaryListRequest request) {
        PageImpl<PersonDiary> pageInfoResult = personDiaryDaoService.findDiaryListByRequest(request);
        List<PersonDiaryListSend> personDiaryListSends = new ArrayList<>();
        pageInfoResult.getContent().forEach(personDiary ->
                personDiaryListSends.add(buildPersonDiaryListSendByPersonDiary(personDiary, Objects.nonNull(recommendDiaryDaoService.findById(personDiary.getId())))));

        return new PageInfo<>(personDiaryListSends,pageInfoResult.getTotalElements());
    }
}
