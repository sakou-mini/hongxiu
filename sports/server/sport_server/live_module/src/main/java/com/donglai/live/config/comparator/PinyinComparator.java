package com.donglai.live.config.comparator;

import com.donglai.model.db.entity.common.User;
import net.sourceforge.pinyin4j.PinyinHelper;

import java.util.Comparator;

/**
 * @author Moon
 * @date 2021-11-01 10:37
 */
public class PinyinComparator implements Comparator<User> {
    @Override
    public int compare(User o1, User o2) {
        char c1 = (o1.getNickname()).charAt(0);
        char c2 = (o2.getNickname()).charAt(0);
        return concatPinyinStringArray(
                PinyinHelper.toHanyuPinyinStringArray(c1)).compareTo(
                concatPinyinStringArray(PinyinHelper
                        .toHanyuPinyinStringArray(c2)));
    }

    private String concatPinyinStringArray(String[] pinyinArray) {
        StringBuffer pinyinSbf = new StringBuffer();
        if ((pinyinArray != null) && (pinyinArray.length > 0)) {
            for (int i = 0; i < pinyinArray.length; i++) {
                pinyinSbf.append(pinyinArray[i]);
            }
        }
        return pinyinSbf.toString();
    }
}
