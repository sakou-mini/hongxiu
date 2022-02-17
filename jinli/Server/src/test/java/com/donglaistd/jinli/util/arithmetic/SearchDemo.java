package com.donglaistd.jinli.util.arithmetic;

import com.donglaistd.jinli.BaseTest;
import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

@ActiveProfiles("test")
public class SearchDemo extends BaseTest {
    @Test
    public void TwoSplitSearchTest(){
        int[] arr = {5, 6, 9, 10, 26, 50, 100};
        int index = findIndex(arr, 50);
        System.out.println(index);
    }

    public int findIndex( int[] arr ,int num){
        int start = 0;
        int end = arr.length -1;
        while (start <= end){
            int index = (start + end) / 2;
            if(num < arr[index]){
                end = index - 1;
            }else if(num >arr[index])
                start = index + 1;
            else{
                return index;
            }
        }
        return -1;
    }

    public static int randomIdx(int[] weight) {
        assert weight.length > 0;
        if(weight.length == 1)
            return 0;
        int[] copy = Arrays.copyOf(weight, weight.length);
        process(copy);
        int v = random(0, copy[copy.length-1]);
        int idx = Arrays.binarySearch(copy, v);
        if(idx >= 0)
            return idx;
        else
            return -(idx+1);
    }
    private static void process(int[] weight) {
        for(int i = 1; i < weight.length; ++i)
            weight[i] = weight[i] + weight[i-1];
    }

    private static int random(int l, int r) {
        if(l == r)
            return l;
        else if(l > r)
            return ThreadLocalRandom.current().nextInt(r, l);
        else
            return ThreadLocalRandom.current().nextInt(l, r);
    }


    @Test
    public void randomWithWeightTest(){
        int[] weight = {20, 20, 3};
        for (int i = 0; i <20 ; i++) {
            int index = randomIdx(weight);
            System.out.println(index);
        }
    }

    @Test
    public void timeTest(){
        long beginningOfWeekOffset = 3600000;
        LocalDate now = LocalDate.now();
        LocalDateTime localDateTime = now.minusDays(now.getDayOfWeek().getValue() - 1).atStartOfDay().plusSeconds(beginningOfWeekOffset / 1000);
        System.out.println(localDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli());
        System.out.println(localDateTime);
    }
}
