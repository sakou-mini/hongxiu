package com.donglai.model.service.impl.es;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.donglai.common.util.StringUtils;
import com.donglai.model.db.entity.blogs.Blogs;
import com.donglai.model.db.service.blogs.BlogsService;
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
public class BlogsElasticsearchServiceImpl implements IElasticsearchService<Blogs> {
    @Autowired
    BlogsService blogsService;
    @Autowired
    RestHighLevelClient esClient;

    public BlogsElasticsearchServiceImpl() {
    }

    public String getIndexName() {
        return "hongxiu" + "_" + Blogs.class.getSimpleName().toLowerCase();
    }

    @Override
    public void initIndex() {
        EsUtil.initIndex(esClient, getIndexName());
    }

    @Override
    public void synCreatIndex() {
        initIndex();
        if (countIndexNum()<=0){
            List<Blogs> allPassBlogs = blogsService.findAllPassBlogs();
            saveIndex(allPassBlogs);
        }
    }

    @Override
    public void saveIndex(List<Blogs> entityList) {
        if(CollectionUtils.isEmpty(entityList)) return;
        String indexName = getIndexName();
        try {
            BulkRequest bulkRequest = new BulkRequest();
            for (Blogs blogs : entityList) {
                bulkRequest.add(new IndexRequest(indexName).id(String.valueOf(blogs.getId())).source(JSON.toJSONString(blogs), XContentType.JSON));
            }
            BulkResponse bulk = esClient.bulk(bulkRequest, RequestOptions.DEFAULT);
            if(bulk.hasFailures()) log.error("es存储失败 {},by data {}",indexName,entityList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Blogs> searchFuzzyQuery(Map<String, String> params) {
        List<Blogs> blogsList = new ArrayList<>(0);
        String resultJson = EsUtil.queryAndResultJson(esClient, getIndexName(), params);
        if (!StringUtils.isNullOrBlank(resultJson)) {
            blogsList = JSONArray.parseArray(resultJson, Blogs.class);
        }
        return blogsList;
    }

    @Override
    public void deleteIndexById(String id) {
        EsUtil.deleteIndexById(esClient, getIndexName(), id);
    }

    @Override
    public void updateIndex(Blogs blogs) {
        try {
            IndexRequest indexRequest = new IndexRequest(getIndexName()).id(String.valueOf(blogs.getId())).source(JSON.toJSONString(blogs), XContentType.JSON);
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
