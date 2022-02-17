/*
package com.donglai.model.service.impl.lucene;

import com.donglai.model.db.entity.blogs.Blogs;
import com.donglai.model.db.service.blogs.BlogsService;
import com.donglai.model.service.ILuceneService;
import com.donglai.model.util.LuceneUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.SearcherManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Component
@Slf4j
public class BlogsLuceneServiceImpl implements ILuceneService<Blogs> {
    @Autowired
    BlogsService blogsService;

    @Autowired
    private IndexWriter indexWriter;
    @Autowired
    private Analyzer analyzer;
    @Autowired
    private SearcherManager searcherManager;

    @Override
    public String getFiledKeyName(String filed) {
        return Blogs.class.getSimpleName() + "_" + filed;
    }

    public String getFiledKeyPrefix() {
        return Blogs.class.getSimpleName() + "_";
    }

    @Override
    public void synCreatIndex() {
        List<Blogs> allPassBlogs = blogsService.findAllPassBlogs();
        createIndex(allPassBlogs);
    }

    public Document createDocument(Blogs blogs) {
        Document doc = new Document();
        doc.add(new StringField(getFiledKeyName("id"), blogs.getStringId(), Field.Store.YES));
        doc.add(new StringField(getFiledKeyName("userId"), blogs.getUserId(), Field.Store.YES));
        doc.add(new TextField(getFiledKeyName("content"), Optional.ofNullable(blogs.getContent()).orElse(""), Field.Store.YES));
        return doc;
    }

    @Override
    public void createIndex(List<Blogs> entityList) {
        try {
            List<Document> docs = new ArrayList<>();
            for (Blogs blogs : entityList) {
                docs.add(createDocument(blogs));
            }
            indexWriter.addDocuments(docs);
            indexWriter.commit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Set<Blogs> searchFuzzyQuery(Map<String, String> param) {
        Set<Blogs> blogsList = new HashSet<>();
        try {
            IndexSearcher indexSearcher = LuceneUtil.getIndexSearcher(searcherManager);
            List<ScoreDoc> hits = LuceneUtil.booleanQuery(indexSearcher, analyzer, param, getFiledKeyPrefix());
            for (ScoreDoc hit : hits) {
                Document doc = indexSearcher.doc(hit.doc);
                String filedKeyName = getFiledKeyName("id");
                Blogs blogs = blogsService.findById(Long.parseLong(doc.get(filedKeyName)));
                if (Objects.nonNull(blogs)) blogsList.add(blogs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return blogsList;
    }

    @Override
    public long deleteIndexById(String id) {
        try {
            long num = indexWriter.deleteDocuments(new Term(getFiledKeyName("id"), id));
            indexWriter.commit();
            return num;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void deleteAll() {
        try {
            // 删除所有的数据
            indexWriter.deleteAll();
            // 提交事物
            indexWriter.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateDocument(Blogs entity) {
        try {
            long num = deleteIndexById(entity.getStringId());
            Document document = createDocument(entity);
            if (num > 0) log.info("删除了索引{}条,剩余文档数{}", num, indexWriter.numDocs());
            indexWriter.addDocument(createDocument(entity));
            indexWriter.commit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
*/
