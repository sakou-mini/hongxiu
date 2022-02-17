package com.donglaistd.jinli.util;

import java.util.ArrayList;
import java.util.logging.Logger;

public abstract class StringNumberUtils {
    private static final Logger logger = Logger.getLogger(StringNumberUtils.class.getName());
    static public boolean isNullOrBlank(String displayName) {
        return displayName == null || displayName.isBlank();
    }

    public static String generateGameId(long count, int length) {
        if(overMaxRange(count,length)) return OrderIdUtils.getOrderNumber();
        if (!checkLength(count, length)) {
            length += 1;
            logger.warning("count too small or too big is not allowed");
            return generateGameId(count, length);
            //throw new RuntimeException("count too small or too big is not allowed");
        }
        StringBuilder stringBuilder = new StringBuilder();
        var accumulate = 0;
        for (int j = 0; j < length; j++) {
            accumulate += countChar(count, j);
            char letter = getLetter(accumulate, getCharacters(RANDOM_STRING[j]));
            int numericValue = Character.getNumericValue(letter);
            if (numericValue == 0 && stringBuilder.toString().isEmpty()) {
                accumulate += 1;
                letter = getLetter(accumulate, getCharacters(RANDOM_STRING[j]));
            }
            stringBuilder.append(letter);
        }
        return stringBuilder.toString();
    }

    private static boolean checkLength(long count, int length) {
        switch (length) {
            case 5:
                return count < 59049;
            case 6:
                return count < 514000;
            case 7:
                return count < 4782969;
        }
        return false;
    }

    private static boolean overMaxRange(long count, int length){
        return length > 7 || count >= 4782969;
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
        return i / (pow9(z)) % 9;
    }

    private static int pow9(int z) {
        return pow9[z];
    }

    private static char getLetter(long i, ArrayList<Character> charList) {
        i %= 9;
        return charList.get((int) i);
    }

    public static int[] pow9 = {1, 9, 9 * 9, 9 * 9 * 9, 9 * 9 * 9 * 9, 9 * 9 * 9 * 9 * 9, 9 * 9 * 9 * 9 * 9 * 9};

    private static final String[] RANDOM_STRING = {"123456789", "768549123", "631975482",
            "742635981", "374562981", "462571389", "367251489"};
}
