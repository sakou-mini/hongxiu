package com.donglai.model.service;

import java.util.List;
import java.util.Map;

public interface IElasticsearchService<T> {
    /*初始化索引*/
    void initIndex();
    void synCreatIndex();

    void saveIndex(List<T> entityList);

    List<T> searchFuzzyQuery(Map<String, String> param);

    void deleteIndexById(String id);

    void deleteAll();

    void updateIndex(T entity);

    long countIndexNum();
}
