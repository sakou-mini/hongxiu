package com.donglaistd.jinli.util;

import com.donglaistd.jinli.BaseTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;
import java.util.logging.Logger;

public class WordFilterUtilTest extends BaseTest {
    private static final java.util.logging.Logger logger = Logger.getLogger(WordFilterUtilTest.class.getName());
    @Autowired
    WordFilterUtil wordFilterUtil;

    @Test
    public void filterWordsTest(){
        StringBuilder sb = new StringBuilder();
        long beginTime = System.currentTimeMillis();
        sb.append("百度文库是百度发布的供网友在线分享文档的平台。百度文库的文档由百度用户上传，需要经过百度的审核才能发布，共产党主义" +
                "百度自身不编辑或修改用户上传的文档内容。梳理威 信,网友可以在线阅读和下载这些文档。百度文库的文档包括教学资料、考试题库、专业资料、公文写作、法律文件等多个领域的资料。百度用户上传文档可以得到一定的积分，下载有标价的文档则需要消耗积分。当前平台支持主流的doc(.docx)、.ppt(.pptx)、.xls(.xlsx)、.pot、.pps、.vsd、.rtf、.wps、.et、.dps、.pdf、.txt文件格式。 [1] \n" +
                "百度文库平台于2009年11月12日推出，2010年7月8日，百度文库手机版上线。2010年11月10日，百度文库文档数量突破1000万。 2011年12月文库优化改版，" +
                "内容专注于教育、PPT、专业文献、应用文书四大领域。＋ ＋ q Q（123123124）2013年11月正式推出文库个人认证项目。截至2014年4月文库文档数量已突破一亿＋＋微信。\n" +
                "2019年5月，原归属于百度EBG（新兴业务事业群）的百度教育事业部被撤裁,垃圾。原百度教育事业部旗下产品百度文库业务进入百度内容生态部门 [2] " +
                "共产主义 。11月7日，百度文库与首都版权产业联盟等单位联合推出版权保护“文源计划”，来源不明力求“为每篇文档找到源头加VX 525872 ");

        Set<String> word = wordFilterUtil.findSensitiveWord(sb.toString(),WordFilterUtil.MAX_MATCH_TYPE);
        logger.info("文本长度："+sb.length());
        logger.info("关键词为："+word);
        long endTime = System.currentTimeMillis();
        logger.info("总共消耗时间为：" + (endTime - beginTime));
        Assert.assertEquals(4,word.size());
    }

    @Test
    public void testFilterNumber() {
        String text = "测试一下数字部分1 2 3 4567456456456或者其他123456456456！56测试一下";
        String replaceText = wordFilterUtil.replaceSensitiveWordAndNumber(text,"*");
        Assert.assertEquals("测试一下数字部分1 2 3 4*", replaceText);

        text = "我的手机号码是152150005668，另外我的生日是1997092038";
        replaceText = wordFilterUtil.replaceSensitiveWordAndNumber(text,"*");
        Assert.assertEquals("我的手机号码是1521*", replaceText);
    }


}
