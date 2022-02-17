package com.donglai.blogs.util;

import com.donglai.common.constant.BannerStatueEnum;
import com.donglai.common.constant.PathConstant;
import com.donglai.common.util.RandomUtil;
import com.donglai.model.db.entity.blogs.Blogs;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.entity.live.Banner;
import com.donglai.model.db.service.blogs.BlogsService;
import com.donglai.model.db.service.common.UserService;
import com.donglai.model.db.service.live.BannerService;
import com.donglai.model.entityBuilder.UserEntityBuilder;
import com.donglai.protocol.Constant;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.donglai.blogs.constant.Constant.*;
import static com.donglai.common.constant.SystemConstant.OFFICIAL_ACCOUNT;
import static com.donglai.common.constant.PathConstant.DEFAULT_BLOGS_VIDEO_BASE_PATH;

/*
    only for test
*/
@Component
@Slf4j
public class MockUtil {

    @Autowired
    BlogsService blogsService;
    @Autowired
    UserService userService;
    @Autowired
    UserEntityBuilder userEntityBuilder;

    public void mockBlogsData() {
        User user;
        String accountId;
        String avatar;
        int startNumber = 0;
        int blogsNumb = 10;
        for (int i = 1; i <= 20; i++) {
            avatar = PathConstant.DEFAULT_AVATAR_BASE_PATH + i + ".jpg";
            accountId = OFFICIAL_ACCOUNT + i;
            user = Optional.ofNullable(userService.findByAccountId(accountId))
                    .orElse(userEntityBuilder.createUser(accountId, avatar, RandomUtil.getRandomJianHan(3), false));
            mockVideoData(user,startNumber,blogsNumb);
            startNumber += blogsNumb;
        }
    }

    public void mockImageBlogsData(User user) {
        List<Blogs> allPassBlogs = blogsService.findAllPassBlogs();
        if (allPassBlogs.size() < 200) {
            List<Blogs> blogsList = new ArrayList<>(200);
            Blogs blogs;
            for (int i = 0; i < 200; i++) {
                blogs = Blogs.newInstance(user.getId(), "content" + i, Constant.BlogsStatus.BLOGS_PASS, Constant.BlogsType.BLOGS_IMAGE, Lists.newArrayList("thumbnail"));
                blogs.setResourceUrl(getRandomDefaultImage());
                blogsList.add(blogs);
            }
            blogsService.saveAll(blogsList);
        }
    }

    public List<String> getRandomDefaultImage() {
        int randomInt = RandomUtil.getRandomInt(1, 9, null);
        List<String> images = new ArrayList<>();
        for (int i = 0; i < randomInt; i++) {
            images.add("/defaultImage/live/img" + i + ".jpg");
        }
        Collections.shuffle(images);
        return images;
    }

    public void mockVideoData(User user,int startNum,int num) {
        List<Blogs> videoBlgos = blogsService.findByUserId(user.getId());
        if (videoBlgos.size() <= 0) {
            List<Blogs> blogsList = new ArrayList<>(10);
            Blogs blogs;
            int resourceCode;
            for (int i = 1; i <= num; i++) {
                resourceCode = startNum + i;
                //blogs = Blogs.newInstance(user.getId(), "官方模拟的动态" + i, Constant.BlogsStatus.BLOGS_PASS, Constant.BlogsType.BLOGS_VIDEO, Lists.newArrayList(getDefaultVideoThumbnails()));
                blogs = Blogs.newInstance(user.getId(), "官方的动态" +resourceCode, Constant.BlogsStatus.BLOGS_PASS, Constant.BlogsType.BLOGS_VIDEO, Lists.newArrayList(getDefaultVideoThumbnails(resourceCode)));
                blogs.setResourceUrl(Lists.newArrayList(DEFAULT_BLOGS_VIDEO_BASE_PATH + resourceCode + VIDEO_MP4_SUFFIX));
                blogsList.add(blogs);
            }
            blogsService.saveAll(blogsList);
        }
    }

    public String getDefaultVideoThumbnails(int thumbName) {
        return "/defaultImage/blogs/thumb/"+thumbName+".jpg";
    }

    @Autowired
    BannerService bannerService;

    public void mockBanner(){
        List<Banner> banners = bannerService.findAllByStatusIsFalseOrderBySortDesc();
        if(!CollectionUtils.isEmpty(banners)) return;
        for (int i = 1; i < 5 ; i++) {
            Banner banner = new Banner("模拟banner" + i, DEFAULT_BANNER_PATH + i + IMG_JPG_SUFFIX, 1, "1", i);
            banner.setStartTime(System.currentTimeMillis());
            banner.setEndTime(banner.getStartTime() + TimeUnit.DAYS.toMillis(2));
            banner.setStatus(BannerStatueEnum.RUNNING.getValue());
            bannerService.save(banner);
        }
        log.info("模拟了banner数据");
    }
}
