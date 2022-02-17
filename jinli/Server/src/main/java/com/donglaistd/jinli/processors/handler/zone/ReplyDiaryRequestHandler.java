package com.donglaistd.jinli.processors.handler.zone;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.DiaryReplyDaoService;
import com.donglaistd.jinli.database.dao.PersonDiaryDaoService;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.component.UserSummaryInfo;
import com.donglaistd.jinli.database.entity.zone.DiaryReply;
import com.donglaistd.jinli.database.entity.zone.PersonDiary;
import com.donglaistd.jinli.processors.handler.MessageHandler;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.VerifyUtil;
import com.google.common.base.Strings;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static com.donglaistd.jinli.Constant.ResultCode.REPLY_NOTFOUND;
import static com.donglaistd.jinli.Constant.ResultCode.SUCCESS;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class ReplyDiaryRequestHandler extends MessageHandler {
    @Autowired
    DiaryReplyDaoService replyDaoService;
    @Autowired
    VerifyUtil verifyUtil;
    @Autowired
    PersonDiaryDaoService diaryDaoService;
    @Autowired
    UserDaoService userDaoService;

    @Override
    @Transactional
    public Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        Jinli.ReplyDiaryRequest request = messageRequest.getReplyDiaryRequest();
        Jinli.ReplyDiaryReply.Builder reply = Jinli.ReplyDiaryReply.newBuilder();
        String diaryId = request.getDiaryId();
        PersonDiary diary = diaryDaoService.findById(diaryId);
        Constant.ResultCode resultCode = verifyUtil.verifyReplyDiary(request, diary);
        if (!resultCode.equals(Constant.ResultCode.SUCCESS))
            return buildReply(reply, resultCode);

        DiaryReply newReply;
        UserSummaryInfo fromUserSummaryInfo = UserSummaryInfo.newInstance(user);
        if (Strings.isNullOrEmpty(request.getReplyId())) {
            newReply = DiaryReply.newInstance(diaryId, request.getText(), fromUserSummaryInfo);
        } else {
            newReply = createChildComments(request, fromUserSummaryInfo);
            if (Objects.isNull(newReply))
                return buildReply(reply, REPLY_NOTFOUND);
        }
        replyDaoService.save(newReply);
        if (!diary.getCommentFlag()) {
            diary.setCommentFlag(true);
            diaryDaoService.save(diary);
        }
        return buildReply(reply.setReply(newReply.toProto()), SUCCESS);
    }

    private DiaryReply createChildComments(Jinli.ReplyDiaryRequest commentParam, UserSummaryInfo fromUserSummary) {
        DiaryReply rootReply = replyDaoService.findById(commentParam.getReplyId());
        if (Objects.isNull(rootReply))
            return null;
        rootReply.addReplyNum();
        DiaryReply comment = DiaryReply.newInstance(commentParam.getDiaryId(), commentParam.getText(), fromUserSummary);
        UserSummaryInfo targetUserSummaryInfo = null;
        if (!Strings.isNullOrEmpty(commentParam.getTargetUserId())) {
            targetUserSummaryInfo = UserSummaryInfo.newInstance(userDaoService.findById(commentParam.getTargetUserId()));
        }
        comment.setLastReply(commentParam.getReplyId(), targetUserSummaryInfo);
        replyDaoService.save(rootReply);
        return comment;
    }
}
