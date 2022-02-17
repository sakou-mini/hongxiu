package com.donglaistd.jinli.util;

import java.util.*;

public class RandomUtil {
    private static final Integer MIN = 1;

    public static Random random = new Random();

    public static int getRandomInt(int begin, int end, Long seed) {
        if (begin == end)
            return begin;
        if(seed == null) random = new Random();
        else random.setSeed(seed);
        return random.nextInt(end - begin + 1) + begin;
    }

    public static boolean randomBool(Long seed){
        int randomInt = getRandomInt(0, 1, seed);
        return randomInt == 1;
    }
    public static List<Integer> getRandomCoinList(int coinAmount, int num, int minCoin, Long seed) {
        List<Integer> randomCoinList = new ArrayList<>();
        int avgCoin = coinAmount / num;
        if (avgCoin <= MIN) {
            for (int i = 0; i < num; i++) randomCoinList.add(MIN);
        } else {
            int curSumCoin = 0;
            for (int i = 0; i < num; i++) {
                int localMinCoin = getRandomInt(MIN, minCoin, seed);
                int reqCoin = (num - i) * localMinCoin;
                int leftMin = coinAmount - reqCoin - curSumCoin;
                int maxCoin;
                if (leftMin <= 0) maxCoin = reqCoin;
                else maxCoin = leftMin;
                int curCoin = getRandomInt(MIN, maxCoin, seed);
                curSumCoin += curCoin;
                randomCoinList.add(curCoin);
            }
        }
        makeUpList(randomCoinList, coinAmount);
        Collections.shuffle(randomCoinList);
        return randomCoinList;
    }

    private static void makeUpList(List<Integer> randomCoinList, int coinAmount) {
        randomCoinList.sort(Integer::compareTo);
        int sum = randomCoinList.stream().mapToInt(Integer::intValue).sum();
        int makeUpCoin = coinAmount - sum;
        if (makeUpCoin == 0)
            return;
        if (makeUpCoin > 0) {
            Integer curMin = randomCoinList.remove(randomCoinList.size()-1);
            curMin += makeUpCoin;
            randomCoinList.add(curMin);
        } else {
            makeUpCoin = Math.abs(makeUpCoin);
            for (int i = randomCoinList.size() - 1; i >= 0; i--) {
                Integer curCoin = randomCoinList.get(i);
                if (curCoin < makeUpCoin) {
                    if (curCoin > MIN * 3) {
                        int decCoin = (int) (curCoin * 0.3);
                        curCoin -= decCoin;
                        makeUpCoin -= decCoin;
                    }
                } else {
                    curCoin -= makeUpCoin;
                    makeUpCoin -= makeUpCoin;
                }
                randomCoinList.set(i, curCoin);
            }
        }
        makeUpList(randomCoinList, coinAmount);
    }

    public static Set<String> randomIds(Collection<String> ids,int num){
        List<String> currentIds = new ArrayList<>(ids);
        Set<String> randomIds = new HashSet<>();
        if(!currentIds.isEmpty()){
            for (int i = 0; i <num; i++) {
                int index = getRandomInt(0, currentIds.size()-1, null);
                randomIds.add(currentIds.remove(index));
                if ( currentIds.size() <= 0 )
                    break;
            }
        }
        return randomIds;
    }
}
