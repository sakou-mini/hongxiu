package com.donglai.account.process;

import com.donglai.model.db.entity.common.Keyword;
import com.donglai.model.db.service.common.KeywordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Moon
 * @date 2021-12-28 13:42
 */
@Component
public class KeywordProcess {
    public final static String ENCODING = "UTF-8";

    @Value("${keyword.file.path}")
    public String configFilePathString;

    @Autowired
    private KeywordService keywordService;

    public void initKeyword() {
        List<Keyword> all = keywordService.findAll();
        //如果为空就初始化
        if (CollectionUtils.isEmpty(all)) {
            Set<String> keyWords = initWordLibrary();
            List<Keyword> keywords = new ArrayList<>();
            keyWords.forEach(v -> {
                Keyword keyword = new Keyword();
                keyword.setWord(v);
                keyword.setStatus(true);
                keyword.setCreatedTime(System.currentTimeMillis());
                keyword.setUpdatedTime(System.currentTimeMillis());
                keywords.add(keyword);
            });
            keywordService.saveAll(keywords);
        }
    }

    private Set<String> initWordLibrary() {
        Set<String> keyWords = null;
        try {
            File file = ResourceUtils.getFile(configFilePathString);
            assert file.exists();
            InputStreamReader read = new InputStreamReader(new FileInputStream(file), ENCODING);
            keyWords = new HashSet<String>();
            BufferedReader br = new BufferedReader(read);
            String txt;
            while ((txt = br.readLine()) != null) keyWords.add(txt);
            br.close();
            read.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return keyWords;
    }
}
