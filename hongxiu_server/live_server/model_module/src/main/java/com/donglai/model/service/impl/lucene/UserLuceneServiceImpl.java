/*
package com.donglai.model.service.impl.lucene;

import com.donglai.common.util.StringUtils;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.service.common.UserService;
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
public class UserLuceneServiceImpl implements ILuceneService<User> {
    @Autowired
    UserService userService;
    @Autowired
    private IndexWriter indexWriter;
    @Autowired
    private Analyzer analyzer;
    @Autowired
    private SearcherManager searcherManager;

    @Override
    public String getFiledKeyName(String filed) {
        return User.class.getSimpleName() + "_" + filed;
    }

    public String getFiledKeyPrefix() {
        return User.class.getSimpleName() + "_";
    }

    @Override
    public void synCreatIndex() {
        List<User> allUser = userService.findAllUser();
        createIndex(allUser);
    }

    public Document createDocument(User user) {
        Document doc = new Document();
        doc.add(new StringField(getFiledKeyName("id"), user.getId() + "", Field.Store.YES));
        doc.add(new StringField(getFiledKeyName("account"), user.getAccountId(), Field.Store.YES));
        if(!StringUtils.isNullOrBlank(user.getNickname()))
            doc.add(new TextField(getFiledKeyName("nickName"), user.getNickname(), Field.Store.YES));
        String signatureText = Optional.ofNullable(user.getSignatureText()).orElse("");
        doc.add(new TextField(getFiledKeyName("signatureText"), signatureText, Field.Store.YES));
        return doc;
    }

    @Override
    public void createIndex(List<User> entityList) {
        try {
            List<Document> docs = new ArrayList<>();
            for (User user : entityList) {
                docs.add(createDocument(user));
            }
            indexWriter.addDocuments(docs);
            indexWriter.commit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Set<User> searchFuzzyQuery(Map<String, String> param) {
        Set<User> userList = new HashSet<>();
        try {
            IndexSearcher indexSearcher = LuceneUtil.getIndexSearcher(searcherManager);
            List<ScoreDoc> hits = LuceneUtil.booleanQuery(indexSearcher, analyzer, param, getFiledKeyPrefix());
            for (ScoreDoc hit : hits) {
                Document doc = indexSearcher.doc(hit.doc);
                String filedKeyName = getFiledKeyName("id");
                User user = userService.findById(doc.get(filedKeyName));
                if (Objects.nonNull(user)) userList.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userList;
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
    public void updateDocument(User user) {
        try {
            long num = deleteIndexById(user.getId());
            if (num > 0) log.info("删除了索引{}条,剩余文档数{}", num, indexWriter.numDocs());
            indexWriter.addDocument(createDocument(user));
            indexWriter.commit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void deleteAll() {
        try {
            // 删除所有的数据
            indexWriter.deleteAll();
            int cnt = indexWriter.numDocs();
            System.out.println("索引条数\t" + cnt);
            // 提交事物
            indexWriter.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
*/
