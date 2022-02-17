package com.donglai.model.service.impl.es;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.donglai.common.util.StringUtils;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.service.common.UserService;
import com.donglai.model.service.IElasticsearchService;
import com.donglai.model.util.EsUtil;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class UserElasticsearchServiceImpl implements IElasticsearchService<User> {
    @Autowired
    UserService userService;
    @Autowired
    RestHighLevelClient esClient;

    public UserElasticsearchServiceImpl() {
    }

    public String getIndexName() {
        return "hongxiu" + "_" + User.class.getSimpleName().toLowerCase();
    }

    @Override
    public void initIndex() {
        EsUtil.initIndex(esClient, getIndexName());
    }

    @Override
    public void synCreatIndex() {
        initIndex();
        if(countIndexNum()<=0){
            List<User> allUser = userService.findAllUser();
            saveIndex(allUser);
        }
    }

    @Override
    public void saveIndex(List<User> entityList) {
        if(CollectionUtils.isEmpty(entityList)) return;
        String indexName = getIndexName();
        try {
            BulkRequest bulkRequest = new BulkRequest();
            for (User user : entityList) {
                bulkRequest.add(new IndexRequest(indexName).id(user.getId()).source(JSON.toJSONString(user), XContentType.JSON));
            }
            BulkResponse bulk = esClient.bulk(bulkRequest, RequestOptions.DEFAULT);
            if(bulk.hasFailures()) log.error("es存储失败 {},by data {}",indexName,entityList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<User> searchFuzzyQuery(Map<String, String> params) {
        List<User> userList = new ArrayList<>(0);
        String resultJson = EsUtil.queryAndResultJson(esClient, getIndexName(), params);
        if (!StringUtils.isNullOrBlank(resultJson)) {
            userList = JSONArray.parseArray(resultJson, User.class);
        }
        return userList;
    }

    @Override
    public void deleteIndexById(String id) {
        EsUtil.deleteIndexById(esClient, getIndexName(), id);
    }

    @Override
    public void updateIndex(User user) {
        try {
            IndexRequest indexRequest = new IndexRequest(getIndexName()).id(user.getId()).source(JSON.toJSONString(user), XContentType.JSON);
            IndexResponse index = esClient.index(indexRequest, RequestOptions.DEFAULT);
            log.info("update dock status:{}",index.status().name());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteAll() {
        EsUtil.cleanIndex(esClient, getIndexName());
    }

    @Override
    public long countIndexNum() {
        return EsUtil.countIndexDocNum(esClient, getIndexName());
    }

}
