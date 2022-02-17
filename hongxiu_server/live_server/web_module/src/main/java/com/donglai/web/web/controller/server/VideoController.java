package com.donglai.web.web.controller.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.donglai.common.util.StringUtils;
import com.donglai.model.db.entity.blogs.Blogs;
import com.donglai.model.db.entity.blogs.BlogsLabels;
import com.donglai.model.db.entity.common.FollowList;
import com.donglai.model.db.service.blogs.BlogsLabelsService;
import com.donglai.model.db.service.blogs.BlogsService;
import com.donglai.model.db.service.common.FollowListService;
import com.donglai.protocol.Constant;
import com.donglai.web.config.LabelJsonConfig;
import com.donglai.web.process.BlogsProcess;
import com.donglai.web.web.dto.video.VideoCallBackDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.*;

/**
 * @author Moon
 * @date 2021-11-10 16:49
 */

@RequestMapping("/api/v1/video")
@RestController
@Slf4j
public class VideoController {
    @Autowired
    private LabelJsonConfig labelJsonConfig;
    @Autowired
    private FollowListService followListService;
    @Autowired
    private BlogsService blogsService;
    @Autowired
    private BlogsLabelsService blogsLabelsService;
    @Autowired
    BlogsProcess blogsProcess;

    @PostMapping("/review/callback")
    public void callback(@RequestBody VideoCallBackDTO videoCallBackDTO) throws IOException {
        String json = videoCallBackDTO.getJson();
        JSONObject jsonObject = JSON.parseObject(json);
        Map<String, String> customInfo = (Map) jsonObject.get("customInfo");
        long id = Long.parseLong(customInfo.get("id"));
        Blogs byId = blogsService.findById(id);
        if (Integer.parseInt(String.valueOf(jsonObject.get("code"))) != 0) {
            log.error("视频审核有误");
            //审核失败
            byId.setBlogsStatus(Constant.BlogsStatus.BLOGS_UNAPPROVED);
            byId.setFailNum(byId.getFailNum() + 1);
            blogsService.save(byId);
            return;
        }

        //色情 0色情 1性感 2正常
        Object pornography = jsonObject.get("54bcfc6c329af61034f7c2fc");
        JSONObject pornographyJson = JSON.parseObject(String.valueOf(pornography));
        //暴恐 0正常
        Object violent = jsonObject.get("5e1d70adeec2874f7318dc52");
        JSONObject violentJson = JSON.parseObject(String.valueOf(violent));
        //政治 0政治人物
        Object politicalFigure = jsonObject.get("5b7be1f59b0c77a8c2afb351");
        JSONObject politicalFigureJson = JSON.parseObject(String.valueOf(politicalFigure));
        //是否 色情 暴恐 政治
        if (Objects.isNull(pornography) || Objects.isNull(violent) || Objects.isNull(politicalFigure)) {
            byId.setBlogsStatus(Constant.BlogsStatus.BLOGS_UNAPPROVED);
            byId.setFailNum(byId.getFailNum() + 1);
            blogsService.save(byId);
            log.error("视频审核有误");
            return;
        }
        if (pornographyJson.size() != 0
                || violentJson.size() != 0
                || politicalFigureJson.size() != 0) {
            blogsProcess.systemAuditBlogsNoPass(byId, "涉及色情/暴恐/政治!");
            return;
        }
        // ------------------标签类------------------
        Set<Object> labels = new HashSet<>();
        //人物细分
        //人物细分字典
        JSONObject characterBreakdownJsonMap = labelJsonConfig.getCharacterBreakdown();
        Object characterBreakdown = jsonObject.get("559e65b3040a5ee37e4505b3");
        JSONObject characterBreakdownJson = JSON.parseObject(String.valueOf(characterBreakdown));
        if (characterBreakdownJson.size() != 0) {
            List<Map<String, Object>> statistics = (List) characterBreakdownJson.get("statistics");
            for (Map<String, Object> statistic : statistics) {
                Object label = characterBreakdownJsonMap.get(statistic.get("label"));
                String[] split = String.valueOf(label).split("/");
                labels.addAll(Arrays.asList(split));
            }

        }
        //场景
        //JSONObject scenesJsonMap = JsonUtil.getJsonMap("scenes.json");
        //Object scenes = jsonObject.get("559e88fa6f7910b622d37fbb");
        //JSONObject scenesJson = JSON.parseObject(String.valueOf(scenes));
        //if(scenesJson.size() != 0){
        //    List<Map<String,Object>> statistics =(List) scenesJson.get("statistics");
        //    for (Map<String, Object> statistic : statistics) {
        //        Object label = scenesJsonMap.get(statistic.get("label"));
        //        str.append("|").append(label);
        //    }
        //}

        //人物行为
        JSONObject characterBehaviorMap = labelJsonConfig.getCharacterBehavior();
        Object characterBehavior = jsonObject.get("5ac4405825146cb63afa643f");
        JSONObject characterBehaviorJson = JSON.parseObject(String.valueOf(characterBehavior));
        if (characterBehaviorJson.size() != 0) {
            List<Map<String, Object>> statistics = (List) characterBehaviorJson.get("statistics");
            for (Map<String, Object> statistic : statistics) {
                Object label = characterBehaviorMap.get(statistic.get("label"));
                String[] split = String.valueOf(label).split("/");
                labels.addAll(Arrays.asList(split));
            }
        }
        Set<Integer> labelsCode = new HashSet<>();
        JSONArray allLabelsMap = labelJsonConfig.getLabelsArray();
        for (Object label : labels) {
            if (!"null".equals(label) && !StringUtils.isNullOrBlank(String.valueOf(label))) {
                for (int i = 0; i < allLabelsMap.size(); i++) {
                    Map<String, String> map = (Map) allLabelsMap.get(i);
                    if (map.get("labelName").equals(label)) {
                        Integer labelsId = Integer.valueOf(map.get("id"));
                        labelsCode.add(labelsId);
                        //添加中间表
                        BlogsLabels blogsLabels = new BlogsLabels();
                        blogsLabels.setBlogsId(id);
                        blogsLabels.setLabelsId(labelsId);
                        blogsLabelsService.save(blogsLabels);
                    }
                }
            }
        }
        labelsCode.addAll(byId.getLabels());
        blogsProcess.systemAuditBlogsPass(byId, labelsCode);

        //添加new标签
        List<FollowList> byLeaderFollower = followListService.findUserFollowers(byId.getUserId());
        for (FollowList followList : byLeaderFollower) {
            followList.setNew(true);
            followListService.save(followList);
        }
    }


    public static void main(String[] args) {
        //Set<Object> list = new HashSet<>();
        //JSONObject characterBreakdownJsonMap = JsonUtil.getJsonMap("characterBreakdown.json");
        //
        //for (Object value : characterBreakdownJsonMap.values()) {
        //    String[] split = String.valueOf(value).split("/");
        //    list.addAll(Arrays.asList(split));
        //}
        //JSONObject characterBehaviorMap = JsonUtil.getJsonMap("characterBehavior.json");
        //for (Object value : characterBehaviorMap.values()) {
        //    String[] split = String.valueOf(value).split("/");
        //    list.addAll(Arrays.asList(split));
        //}
        //List<Object> objects = new ArrayList<>(Collections.emptyList());
        //objects.addAll(list);
        //Map<String,String> map = new HashMap<>();
        //for (int i = 0; i < list.size(); i++) {
        //    map.put(String.valueOf(i),objects.get(i).toString());
        //}
        //System.out.println(JSON.toJSON(map));
    }
}
