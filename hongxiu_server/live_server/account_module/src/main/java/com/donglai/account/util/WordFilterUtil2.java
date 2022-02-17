/*
package com.donglai.account.util;

import com.donglai.common.util.StringUtils;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class WordFilterUtil2 {
    public final static String ENCODING = "UTF-8";

    public static int MIN_MATCH_TYPE = 1;

    public static int MAX_MATCH_TYPE = 2;
    public static int replaceWordLength = 3;

    @Value("${keyword.text.number.max.length}")
    public int maxNumberLength;

    private Set<String> keyWords;

    private Map keyWordMap;

    public WordFilterUtil2(@Value("${keyword.file.path}") final String configFilePath) {
        initWordLibrary(configFilePath);
    }

    public Set<String> getKeyWords() {
        return keyWords;
    }

    public void initWordLibrary(String filePath) {
        try {
            File file = ResourceUtils.getFile(filePath);
            assert file.exists();
            InputStreamReader read = new InputStreamReader(new FileInputStream(file), ENCODING);
            keyWords = new HashSet<String>();
            BufferedReader br = new BufferedReader(read);
            String txt;
            while ((txt = br.readLine()) != null) keyWords.add(txt);
            br.close();
            read.close();
            initKeyWordsMap(keyWords);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initKeyWordsMap(Set<String> wordSet) {
        this.keyWordMap = new HashMap(wordSet.size());
        for (String word : wordSet) {
            Map nowMap = keyWordMap;
            for (int i = 0; i < word.length(); i++) {
                char keyChar = word.charAt(i);
                Object tempMap = nowMap.get(keyChar);
                if (tempMap != null) {
                    nowMap = (Map) tempMap;
                } else {
                    Map<String, String> newMap = new HashMap<String, String>();
                    newMap.put("isEnd", "0");
                    nowMap.put(keyChar, newMap);
                    nowMap = newMap;
                }
                if (i == word.length() - 1) {
                    nowMap.put("isEnd", "1");
                }
            }
        }
    }

    public int CheckSensitiveWord(String txt, int beginIndex, int matchType) {
        boolean flag = false;
        int matchFlag = 0;
        Map nowMap = keyWordMap;
        for (int i = beginIndex; i < txt.length(); i++) {
            char word = txt.charAt(i);
            nowMap = (Map) nowMap.get(word);
            if (nowMap != null) {
                matchFlag++;
                if ("1".equals(nowMap.get("isEnd"))) {
                    flag = true;
                    if (MIN_MATCH_TYPE == matchType) break;
                }
            } else break;
        }
        if (matchFlag < 2 && !flag) matchFlag = 0;  //REQUEST 1 words
        return matchFlag;
    }

    public Set<String> findSensitiveWord(String txt, int matchType) {
        Set<String> sensitiveWordList = new HashSet<String>();
        for (int i = 0; i < txt.length(); i++) {
            int length = CheckSensitiveWord(txt, i, matchType);
            if (length > 0) {
                sensitiveWordList.add(txt.substring(i, i + length));
                i = i + length - 1;
            }
        }
        return sensitiveWordList;
    }

    public boolean containSensitiveWord(String txt) {
        return findSensitiveWord(txt, MIN_MATCH_TYPE).size() > 0;
    }

    public String replaceSensitiveWord(String txt, String replaceWord) {
        String formattingText = txt;
        for (int i = 0; i < txt.length(); i++) {
            int length = CheckSensitiveWord(txt, i, MAX_MATCH_TYPE);
            if (length > 0) {
                String words = txt.substring(i, i + length);
                System.out.println(words + "-->size is" + words.length());
                formattingText = formattingText.replaceFirst(words, replaceWord.repeat(replaceWordLength));
                i = i + length - 1;
            }
        }
        return formattingText;
    }

    public String replaceSensitiveWordAndNumber(String txt, String replaceWord) {
        String formatText = replaceSensitiveWord(txt, replaceWord);
        return formatNumberByLength(formatText);
    }

    public String formatNumberByLength(String text) {
        String[] ss = text.split("\\D+");
        int totalNumBerSize = Lists.newArrayList(ss).stream().filter(number -> !StringUtils.isNullOrBlank(number)).mapToInt(String::length).sum();
        if (totalNumBerSize < maxNumberLength)
            return text;
        int numberSize = 0;
        String[] words = text.split("");
        String word;
        for (int i = 0; i < words.length; i++) {
            word = words[i];
            if (StringUtils.isNumber(word)) {
                numberSize++;
            }
            if (numberSize >= maxNumberLength) {
                return text.substring(0, i) + "*";
            }
        }
        return text;
    }
}
*/
