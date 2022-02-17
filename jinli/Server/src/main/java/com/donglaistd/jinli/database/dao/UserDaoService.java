package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.dao.backoffice.UserDataStatisticsDaoService;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.http.dto.request.UserListRequest;
import com.donglaistd.jinli.http.entity.PageInfo;
import com.donglaistd.jinli.util.CommonCriteriaUtil;
import com.donglaistd.jinli.util.StringUtils;
import com.donglaistd.jinli.util.VerifyUtil;
import com.google.common.collect.Lists;
import com.mongodb.client.result.UpdateResult;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

import static com.donglaistd.jinli.Constant.AccountStatue.ACCOUNT_BAN;
import static com.donglaistd.jinli.Constant.AccountStatue.ACCOUNT_NORMAL;
import static com.donglaistd.jinli.Constant.LiveStatus.*;
import static com.donglaistd.jinli.constant.GameConstant.HTTP_RESULT_USER_IS_LIVEUSER;

@Service
@Transactional
public class UserDaoService {
    private final UserRepository userRepository;
    public UserDaoService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Autowired
    UserDataStatisticsDaoService userDataStatisticsDaoService;
    @Autowired
    MongoOperations mongoOperations;
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    VerifyUtil verifyUtil;
    @Autowired
    LiveUserDaoService liveUserDaoService;

    @Cacheable(cacheNames = "c1")
    public User findByAccountName(String accountName) {
        Assert.notNull(accountName, "Name must not be null");
        return this.userRepository.findByAccountName(accountName);
    }

    @CachePut(cacheNames = "c1", key = "#user.getAccountName()")
    public User save(User user) {
        Assert.notNull(user, "User must not be null");
        Assert.notNull(user.getDisplayName(), "User display name must not be null");
        Assert.notNull(user.getAccountName(), "User name must not be null");
        Assert.notNull(user.getToken(), "User token must not be null");
        return this.userRepository.save(user);
    }

    public boolean existByAccountNameCaseInsensitive(String accountName) {
        return userRepository.existsByAccountName(accountName);
    }

    protected void deleteAll() {
        this.userRepository.deleteAll();
    }

    public List<User> findByPageAndSortByLevel(int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "level");
        Pageable pageable = PageRequest.of(page, size, sort);
        return userRepository.findAll(pageable).getContent();
    }

    public User findById(String userId) {
        return userRepository.findById(userId).orElse(null);
    }

    public void saveAll(List<User> users) {
        userRepository.saveAll(users);
    }

    public User findByLiveUserId(String id) {
        return userRepository.findByLiveUserId(id);
    }

    public long count() {
        return userRepository.count();
    }

    public long countAllActiveUser() {
        List<String> liveUserIds = liveUserDaoService.findAllPassLiveUser().stream().map(LiveUser::getId).collect(Collectors.toList());
        return userRepository.countByLastLoginTimeGreaterThanAndLiveUserIdNotIn(0,liveUserIds);
    }

    public long countNormalActiveUserNumByPlatform(Constant.PlatformType platform){
        List<String> liveUserIds = liveUserDaoService.findAllPassLiveUser().stream().map(LiveUser::getId).collect(Collectors.toList());
        return userRepository.countByPlatformTypeAndLastLoginTimeGreaterThanAndLiveUserIdNotIn(platform,0,liveUserIds);
    }

    public boolean existByDisplayName(String displayName) {
        return userRepository.existsByDisplayName(displayName);
    }

    public boolean existsByDisplayNameAndAccountNameIsNot(String displayName,String accountName) {
        return userRepository.existsByDisplayNameAndAccountNameIsNot(displayName,accountName);
    }

    public UpdateResult increaseGameCoin(String userId, long increaseAmount) {
        return mongoOperations.updateFirst(new Query(Criteria.where("_id").is(userId)), new Update().inc("gameCoin", increaseAmount), User.class);
    }

    public UpdateResult increaseGoldBean(String userId, long increaseAmount) {
        return mongoOperations.updateFirst(new Query(Criteria.where("_id").is(userId)), new Update().inc("goldBean", increaseAmount), User.class);
    }

    public User findByDisplayName(String displayName) {
        return userRepository.findByDisplayName(displayName);
    }

    public User findByPhoneNumber(String phone) {
        return userRepository.findByPhoneNumber(phone);
    }

    public boolean existByPhoneNumber(String phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }

    public PageImpl<User> findAllByPage(int page, int size) {
        Criteria criteria = Criteria.where("scriptUser").is(false);
        Query query = Query.query(criteria).with(Sort.by(Sort.Direction.DESC,"lastLoginTime"));
        Pageable thePage = PageRequest.of(page, size);
        long count = mongoTemplate.count(query, User.class);
        return new PageImpl<>(mongoTemplate.find(query.with(thePage), User.class), PageRequest.of(page, size), count);
    }

    public User findIdAndPhoneNumberPage(String id, String phoneNumber) {
        return userRepository.findByIdAndPhoneNumber(id, phoneNumber);
    }

    public List<User> findByUserIds(List<String> userIds) {
        return userRepository.findByIdIn(userIds);
    }


    public PageInfo<User> getUserListByCondition(String userId , String phone, String ip, Constant.AccountStatue statue, int page, int size, Constant.PlatformType platformType){
        PageRequest pageRequest = PageRequest.of(page, size);
        LookupOperation attribute = LookupOperation.newLookup().from("userAttribute").localField("_id").foreignField("_id").as("attribute");
        LookupOperation liveUser = LookupOperation.newLookup().from("liveUser").localField("liveUserId").foreignField("_id").as("liveUser");
        Criteria criteria = Criteria.where("scriptUser").is(false).and("platformType").is(platformType).and("liveUser.liveStatus").nin(OFFLINE, ONLINE, LIVE_BAN).and("lastLoginTime").gt(0);
        criteria = Objects.equals(ACCOUNT_NORMAL, statue) ? criteria.and("attribute.statue").nin(ACCOUNT_BAN) : criteria.and("attribute.statue").is(statue);
        if(!StringUtil.isNullOrEmpty(userId)){
            User user = findUserByPlatformUserIdOrUserId(userId);
            if(Objects.isNull(user)) return new PageInfo<>(Lists.newArrayList(), 0);
            if(verifyUtil.checkIsLiveUser(user)) return new PageInfo<>(new ArrayList<>(0), 0, HTTP_RESULT_USER_IS_LIVEUSER);
            criteria = CommonCriteriaUtil.getUserCriteriaByUserId(criteria, userId, platformType);
        }
        if(!StringUtil.isNullOrEmpty(phone)){
            User user = findByPhoneNumber(phone);
            if(Objects.isNull(user)) return new PageInfo<>(Lists.newArrayList(), 0);
            if(verifyUtil.checkIsLiveUser(user)) return new PageInfo<>(new ArrayList<>(0), 0, HTTP_RESULT_USER_IS_LIVEUSER);
            criteria.and("phoneNumber").is(phone);
        }
        if(!StringUtil.isNullOrEmpty(ip)){
            criteria.and("lastIp").is(ip);
        }
        return pageQuery(criteria, attribute, liveUser, pageRequest,"createDate");
    }

    public long countUserCoinByUserIds(List<String> userIds){
        List<User> users = findByUserIds(userIds);
        return users.stream().mapToLong(User::getGameCoin).sum();
    }

    public Map<Constant.PlatformType, List<String>> groupUserInfoByPlatformType(){
        List<User> users = userRepository.findAllByScriptUserIs(false);
        Map<Constant.PlatformType, List<String>> platformUsers = new HashMap<>();
        for (User user : users) {
            platformUsers.computeIfAbsent(user.getPlatformType(), key -> new ArrayList<>()).add(user.getId());
        }
        return platformUsers;
    }

    public PageInfo<User> findBanUser(int platform, String userId, String phoneNumber ,String ip , int page, int size,Long startTime ,Long endTime) {
        Constant.PlatformType platformType = Constant.PlatformType.forNumber(platform);
        PageRequest pageRequest = PageRequest.of(page, size);
        LookupOperation attribute = LookupOperation.newLookup().from("userAttribute").localField("_id").foreignField("_id").as("attribute");
        LookupOperation liveUser = LookupOperation.newLookup().from("liveUser").localField("liveUserId").foreignField("_id").as("liveUser");
        Criteria criteria = Criteria.where("scriptUser").is(false).and("platformType").is(platformType).and("liveUser.liveStatus").nin(OFFLINE, ONLINE, LIVE_BAN)
                .and("attribute.statue").is(ACCOUNT_BAN);
        List<Criteria> criteriaList = new ArrayList<>();
        if (!StringUtil.isNullOrEmpty(userId)){
            criteria.and("_id").is(userId);
        }
        if(!StringUtil.isNullOrEmpty(phoneNumber)){
            criteria.and("phoneNumber").is(phoneNumber);
        }
        if(!StringUtil.isNullOrEmpty(ip)){
            criteria.and("lastIp").is(ip);
        }
        if(Objects.nonNull(startTime)) {
            criteriaList.add(Criteria.where("attribute.time").gte(startTime));
        }
        if(Objects.nonNull(endTime)) {
            criteriaList.add(Criteria.where("attribute.time").lte(endTime));
        }
        if(!criteriaList.isEmpty())
            criteria.andOperator(criteriaList.toArray(new Criteria[0]));
        return pageQuery(criteria, attribute, liveUser, pageRequest,"createDate");
    }

    private PageInfo<User> pageQuery( Criteria criteria , LookupOperation attribute, LookupOperation liveUser, PageRequest pageRequest,String sortedFiled) {
        int totalNum = mongoTemplate.aggregate(Aggregation.newAggregation(attribute, liveUser, Aggregation.match(criteria)), User.class, User.class).getMappedResults().size();
        Aggregation agg;
        if(Objects.nonNull(pageRequest)){
            int page = pageRequest.getPageNumber();
            int size = pageRequest.getPageSize();
            agg = Aggregation.newAggregation(attribute, liveUser, Aggregation.match(criteria),Aggregation.sort(Sort.by(Sort.Direction.DESC,sortedFiled)),
                    Aggregation.skip(page > 1 ? (page - 1) * size : 0L), Aggregation.limit(size));
        }else{
            agg = Aggregation.newAggregation(attribute, liveUser, Aggregation.match(criteria),Aggregation.sort(Sort.by(Sort.Direction.DESC,sortedFiled)));
            pageRequest = PageRequest.of(0, totalNum);
        }
        return new PageInfo<>( mongoTemplate.aggregate(agg, User.class, User.class).getMappedResults(), pageRequest, totalNum);
    }

    public long countBanUserByTimeAndPlatform(Constant.PlatformType platform, long startTime, long endTime) {
        LookupOperation attribute = LookupOperation.newLookup().from("userAttribute").localField("_id").foreignField("_id").as("attribute");
        Criteria criteria = Criteria.where("scriptUser").is(false).and("platformType").is(platform).and("attribute.statue").is(ACCOUNT_BAN)
                .and("attribute.time").gte(startTime).lte(endTime);
        return mongoTemplate.aggregate(Aggregation.newAggregation(attribute, Aggregation.match(criteria)), User.class, User.class).getMappedResults().size();
    }

    public PageInfo<User> findByUserListRequest(UserListRequest request) {
        LookupOperation liveUser = LookupOperation.newLookup().from("liveUser").localField("liveUserId").foreignField("_id").as("liveUser");
        LookupOperation attribute = LookupOperation.newLookup().from("userAttribute").localField("_id").foreignField("_id").as("attribute");
        Criteria criteria = Criteria.where("scriptUser").is(false).and("platformType").is(request.getPlatformType()).and("liveUser.liveStatus").nin(OFFLINE, ONLINE, LIVE_BAN);
        List<Criteria> criteriaList = new ArrayList<>();
        if (!StringUtil.isNullOrEmpty(request.getUserId())){
            User user = findUserByPlatformUserIdOrUserId(request.getUserId());
            if(Objects.isNull(user)) return new PageInfo<>(Lists.newArrayList(), 0);
            criteria.and("_id").is(user.getId());
        }
        if (!StringUtil.isNullOrEmpty(request.getDisplayName())){
            criteria.and("displayName").is(request.getDisplayName());
        }
        if(Objects.nonNull(request.getStartTime())) {
            criteriaList.add(Criteria.where("lastLoginTime").gte(request.getStartTime()));
        }else{
            criteriaList.add(Criteria.where("lastLoginTime").gt(0));
        }
        if(Objects.nonNull(request.getEndTime())) {
            criteriaList.add(Criteria.where("lastLoginTime").lte(request.getEndTime()));
        }
        if(!Objects.equals(request.getStartOfLiveWatchTime(),request.getEndOfLiveWatchTime()) || !Objects.equals(0L,request.getStartOfLiveWatchTime())){
            if(Objects.nonNull(request.getStartOfLiveWatchTime()) && request.getStartOfLiveWatchTime()>0){
                criteriaList.add(Criteria.where("attribute.watchLiveTime").gte(request.getStartOfLiveWatchTime()));
            }
            if(Objects.nonNull(request.getEndOfLiveWatchTime())){
                criteriaList.add(Criteria.where("attribute.watchLiveTime").lte(request.getEndOfLiveWatchTime()));
            }
        }
        if(request.getStatueType()!=null){
            criteriaList.add(Criteria.where("attribute.statue").is(request.getStatueType()));
        }
        if(!StringUtils.isNullOrBlank(request.getIp())){
            criteriaList.add(Criteria.where("lastIp").is(request.getIp()));
        }
        criteria = criteria.andOperator(criteriaList.toArray(new Criteria[0]));
        return pageQuery(criteria, attribute, liveUser, request.getPageRequest(),"lastLoginTime");
    }

    public User findUserByPlatformUserIdOrUserId(String id){
        User user = userRepository.findByPlatformUserId(id);
        if(user == null) user = userRepository.findById(id).orElse(null);
        return user;
    }

    public List<User> findAll(){
        return userRepository.findAll();
    }
}