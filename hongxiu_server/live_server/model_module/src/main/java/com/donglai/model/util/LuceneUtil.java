package com.donglai.model.util;

import com.donglai.common.util.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LuceneUtil {
    public static IndexSearcher getIndexSearcher(SearcherManager searcherManager) {
        try {
            searcherManager.maybeRefresh();
            return searcherManager.acquire();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<ScoreDoc> booleanQuery(IndexSearcher indexSearcher, Analyzer analyzer, Map<String, String> param, String keyPrefix) {
        List<ScoreDoc> hits = new ArrayList<>();
        try {
            BooleanQuery.Builder builder = new BooleanQuery.Builder();
            for (Map.Entry<String, String> paramEntry : param.entrySet()) {
                if (!StringUtils.isNullOrBlank(paramEntry.getValue())) {
                    String filedKeyName = keyPrefix + paramEntry.getKey();
                    builder.add(new QueryParser(filedKeyName, analyzer).parse(paramEntry.getValue()), BooleanClause.Occur.SHOULD);
                }
            }
            TopDocs search = indexSearcher.search(builder.build(), Integer.MAX_VALUE);
            hits = Arrays.stream(search.scoreDocs).collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hits;
    }
}
