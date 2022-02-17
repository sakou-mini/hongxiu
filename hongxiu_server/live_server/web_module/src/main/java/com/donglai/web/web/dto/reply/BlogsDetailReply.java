package com.donglai.web.web.dto.reply;

import com.donglai.model.db.entity.blogs.Blogs;
import com.donglai.model.db.entity.common.User;
import com.donglai.web.db.backoffice.entity.BackOfficeUser;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BlogsDetailReply {
    private Blogs blogs;

    private UserSummaryInfo userInfo;

    private BackOfficeUser approvedBackofficeUser;

    public BlogsDetailReply(Blogs blogs, User user, BackOfficeUser approvedBackofficeUser) {
        this.blogs = blogs;
        this.approvedBackofficeUser = approvedBackofficeUser;
        if (user != null) {
            this.userInfo = new UserSummaryInfo(user.getNickname(), user.getId());
        }
    }

    @Data
    @NoArgsConstructor
    public static class UserSummaryInfo {
        private String userNickname;
        private String userId;

        public UserSummaryInfo(String userNickname, String userId) {
            this.userNickname = userNickname;
            this.userId = userId;
        }
    }
}
