/*

package com.donglai.model.config.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.ControlledRealTimeReopenThread;
import org.apache.lucene.search.SearcherFactory;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class LuceneConfig {
    private static final String LUCENEINDEXPATH = "json/indexDir/";

    */
/**
     * 创建一个 Analyzer 实例 (中文智能分词器)
     *//*

    @Bean
    public Analyzer analyzer() {
        return new SmartChineseAnalyzer();
    }

    */
/**
     * 索引位置
     *//*

    @Bean
    public Directory userDirectory() throws IOException {

        Path path = Paths.get(LUCENEINDEXPATH);
        File file = path.toFile();
        if (!file.exists()) {
            //如果文件夹不存在,则创建
            file.mkdirs();
        }
        return FSDirectory.open(path);
    }

    */
/**
     * 创建indexWriter
     *//*

    @Bean
    public IndexWriter indexWriter(Directory directory, Analyzer analyzer) throws IOException {
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);
        // 清空索引
       */
/* indexWriter.deleteAll();
        indexWriter.commit();*//*

        return indexWriter;
    }

    */
/**
     * SearcherManager管理
     *//*

    @Bean
    public SearcherManager searcherManager(Directory directory, IndexWriter indexWriter) throws IOException {
        SearcherManager searcherManager = new SearcherManager(indexWriter, false, false, new SearcherFactory());
        ControlledRealTimeReopenThread cRTReopenThead = new ControlledRealTimeReopenThread(indexWriter, searcherManager,
                5.0, 0.025);
        cRTReopenThead.setDaemon(true);
        //线程名称
        cRTReopenThead.setName("更新IndexReader线程");
        // 开启线程
        cRTReopenThead.start();
        return searcherManager;
    }

}
*/
