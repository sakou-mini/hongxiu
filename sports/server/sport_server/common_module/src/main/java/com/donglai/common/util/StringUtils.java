package com.donglai.common.util;

import java.util.ArrayList;
import java.util.UUID;
import java.util.regex.Pattern;

public abstract class StringUtils {

    static public boolean isNullOrBlank(String str) {
        return str == null || str.isBlank();
    }

    public static String generateStringName(long count) {
        if (count < 0 || count > 2176782336L) {
            throw new RuntimeException("count too small or too big is not allowed");
        }
        StringBuilder stringBuilder = new StringBuilder();
        var accumulate = 0;
        for (int j = 0; j < 6; j++) {
            accumulate += countChar(count, j);
            stringBuilder.append(getLetter(accumulate, getCharacters(RANDOM_STRING[j])));
        }
        return stringBuilder.toString();
    }

    private static ArrayList<Character> getCharacters(String s) {
        var chars = s.toCharArray();
        var charList = new ArrayList<Character>();
        for (var c : chars) {
            charList.add(c);
        }
        return charList;
    }

    private static long countChar(long i, int z) {
        return i / (pow36(z)) % 36;
    }

    private static int pow36(int z) {
        return pow36[z];
    }

    private static char getLetter(long i, ArrayList<Character> charList) {
        i %= 36;
        return charList.get((int) i);
    }

    public static int[] pow36 = {1, 36, 36 * 36, 36 * 36 * 36, 36 * 36 * 36 * 36, 36 * 36 * 36 * 36 * 36};

    private static final String[] RANDOM_STRING = {"QIO013PG5DTBKF8RNJEY62C9ZHSXAL4M7WUV", "MF730U6J15CDBTKEA4XLZ2IVWQH98RSPNYGO", "YT5ZLCD1J6PIRWH93AFX7E2QG8UNVM04BKOS",
            "T8LUI01JY29MABFQWEVGRPH47KCDSO6XNZ35", "2PDSR4HBXJQ8AVGYO73ZLK91IT5F0N6UWCEM", "6YBXARZU7185WCISM9KLJH4PF2GNVOT3E0QD"};

    public static boolean checkAccountName(String accountName) {
        if (accountName.length() < 4 || accountName.length() > 16) {
            return false;
        }
        String pattern = "[A-Za-z]\\w+";
        return Pattern.matches(pattern, accountName);
    }

    public static boolean isNumber(String str) {
        Pattern pattern = Pattern.compile("^[-+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    public static String createUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static int getWordCount(String wordCount)
    {
        wordCount = wordCount.replaceAll("[^\\x00-\\xff]", "**");
        return wordCount.length();
    }
}
