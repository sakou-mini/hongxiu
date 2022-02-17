package com.donglai.model.util;

import com.alibaba.fastjson.JSONArray;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
public class EsUtil {
    private static final int MAX_SIZE = 1000;
    /*初始化索引*/
    public static void initIndex(RestHighLevelClient esClient,String indexName){
        try {
            GetIndexRequest getIndexRequest = new GetIndexRequest(indexName);
            boolean exists = esClient.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
            if(!exists){
                //创建索引
                CreateIndexRequest request = new CreateIndexRequest(indexName);
                esClient.indices().create(request, RequestOptions.DEFAULT);
                log.info("info 初始化了Es  {} 索引库", indexName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*查询并返回json字符串*/
    public static String queryAndResultJson(RestHighLevelClient esClient,String indexName, Map<String, String> params){
        try {
            SearchRequest searchRequest = new SearchRequest(indexName);
            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
            // 查询条件，使用QueryBuilders快速匹配
            // termQuery 精确匹配
            // matchAllQuery 匹配所有
            //matchQuery 模糊匹配
            params.forEach((param,value)->{
                boolQuery.filter(QueryBuilders.matchQuery(param, value));
            });
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder().query(boolQuery);
            sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS)).size(MAX_SIZE);
            searchRequest.source(sourceBuilder);
            SearchResponse search = esClient.search(searchRequest, RequestOptions.DEFAULT);
            return JSONArray.toJSONString(Arrays.stream(search.getHits().getHits()).map(SearchHit::getSourceAsMap).collect(Collectors.toList()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /*删除文档*/
    public static void deleteIndexById(RestHighLevelClient esClient, String indexName, String id){
        try {
            DeleteRequest deleteRequest = new DeleteRequest(indexName, id);
            DeleteResponse delete = esClient.delete(deleteRequest, RequestOptions.DEFAULT);
            log.info("delete index by {} id {} status {}",indexName,id, delete.status());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*清空索引库*/
    public static void cleanIndex(RestHighLevelClient esClient, String indexName) {
        try {
            DeleteByQueryRequest deleteByQueryRequest = new DeleteByQueryRequest(indexName).setQuery(QueryBuilders.matchAllQuery());
            BulkByScrollResponse deleteByQueryRes = esClient.deleteByQuery(deleteByQueryRequest, RequestOptions.DEFAULT);
            log.info("clean index {} status:{}",indexName, deleteByQueryRes.getStatus());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static long countIndexDocNum(RestHighLevelClient esClient, String indexName) {
        try {
            CountRequest countRequest = new CountRequest(indexName).query(QueryBuilders.matchAllQuery());
            return esClient.count(countRequest, RequestOptions.DEFAULT).getCount();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
