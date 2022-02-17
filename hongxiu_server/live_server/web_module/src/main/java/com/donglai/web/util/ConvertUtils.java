package com.donglai.web.util;

import com.donglai.common.util.CombineBeansUtil;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.entity.common.Keyword;
import com.donglai.model.db.entity.live.ReportComment;
import com.donglai.model.db.entity.live.ReportUser;
import com.donglai.model.db.entity.live.ReportVideo;
import com.donglai.model.db.entity.live.FeedBack;
import com.donglai.web.db.backoffice.entity.BackOfficeUser;
import com.donglai.web.web.vo.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Moon
 * @date 2021-12-21 14:29
 */
public class ConvertUtils {
    public static List<UserVO> userListToUserVOList(List<User> users) {
        List<UserVO> res = new ArrayList<>();
        for (User user : users) {
            res.add(userToUserVO(user));
        }
        return res;
    }

    public static UserVO userToUserVO(User user) {
        CombineBeansUtil<UserVO> combineBeansUtil = new CombineBeansUtil<>(UserVO.class);
        return combineBeansUtil.combineBeans(user);
    }


    public static TouristVO userToTouristVO(User user) {
        CombineBeansUtil<TouristVO> combineBeansUtil = new CombineBeansUtil<>(TouristVO.class);
        return combineBeansUtil.combineBeans(user);
    }

    public static List<TouristVO> userListToTouristVOList(List<User> users) {
        List<TouristVO> res = new ArrayList<>();
        for (User user : users) {
            res.add(userToTouristVO(user));
        }
        return res;
    }

    public static ReportVideoVO recordVideoToRecordVideoVO(ReportVideo record) {
        CombineBeansUtil<ReportVideoVO> combineBeansUtil = new CombineBeansUtil<>(ReportVideoVO.class);
        return combineBeansUtil.combineBeans(record);
    }

    public static List<ReportVideoVO> recordVideoListToRecordVideoVOList(List<ReportVideo> records) {
        List<ReportVideoVO> res = new ArrayList<>();
        for (ReportVideo record : records) {
            res.add(recordVideoToRecordVideoVO(record));
        }
        return res;
    }

    public static ReportCommentVO recordCommentToRecordCommentVO(ReportComment record) {
        CombineBeansUtil<ReportCommentVO> combineBeansUtil = new CombineBeansUtil<>(ReportCommentVO.class);
        return combineBeansUtil.combineBeans(record);
    }

    public static List<ReportCommentVO> recordCommentListToRecordCommentVOList(List<ReportComment> records) {
        List<ReportCommentVO> res = new ArrayList<>();
        for (ReportComment record : records) {
            res.add(recordCommentToRecordCommentVO(record));
        }
        return res;
    }


    public static ReportUserVO recordUserToRecordUserVO(ReportUser content) {
        CombineBeansUtil<ReportUserVO> combineBeansUtil = new CombineBeansUtil<>(ReportUserVO.class);
        return combineBeansUtil.combineBeans(content);
    }

    public static List<ReportUserVO> recordUserListToRecordUserVOList(List<ReportUser> content) {
        List<ReportUserVO> res = new ArrayList<>();
        for (ReportUser record : content) {
            res.add(recordUserToRecordUserVO(record));
        }
        return res;
    }


    public static KeywordVO keywordToKeywordVO(Keyword keyword) {
        CombineBeansUtil<KeywordVO> combineBeansUtil = new CombineBeansUtil<>(KeywordVO.class);
        return combineBeansUtil.combineBeans(keyword);
    }

    public static List<KeywordVO> keywordListToKeywordVOList(List<Keyword> content) {
        List<KeywordVO> res = new ArrayList<>();
        for (Keyword keyword : content) {
            res.add(keywordToKeywordVO(keyword));
        }
        return res;
    }

    public static FeedBackVO feedBackToFeedBackVO(FeedBack feedBack) {
        CombineBeansUtil<FeedBackVO> combineBeansUtil = new CombineBeansUtil<>(FeedBackVO.class);
        return combineBeansUtil.combineBeans(feedBack);
    }

    public static List<FeedBackVO> feedBackListToFeedBackVOList(List<FeedBack> feedBacks) {
        List<FeedBackVO> res = new ArrayList<>();
        for (FeedBack feedBack : feedBacks) {
            res.add(feedBackToFeedBackVO(feedBack));
        }
        return res;
    }

    public static BackOfficeUserVO backOfficeUserToBackOfficeUserVO(BackOfficeUser content) {
        CombineBeansUtil<BackOfficeUserVO> combineBeansUtil = new CombineBeansUtil<>(BackOfficeUserVO.class);
        return combineBeansUtil.combineBeans(content);
    }

    public static List<BackOfficeUserVO> backOfficeUserListToBackOfficeUserVOList(List<BackOfficeUser> content) {
        List<BackOfficeUserVO> res = new ArrayList<>();
        for (BackOfficeUser backOfficeUser : content) {
            res.add(backOfficeUserToBackOfficeUserVO(backOfficeUser));
        }
        return res;
    }

    public static void main(String[] args) {
        User user = new User();
        user.setStatus(false);
        user.setBirthday(11111);
        System.out.println(userToUserVO(user));
    }
}
