package com.donglaistd.jinli.util.arithmetic;

import java.util.ArrayList;
import java.util.Random;

public class NoRepeatRandom extends Random {

    private ArrayList<Integer> recodedList = new ArrayList<>();

    public int nextIntNoRepeat() {
        int r;
        while(recodedList.contains(r = super.nextInt())){}
        recodedList.add(r);
        return r;
    }

    public int nextIntNoRepeat(int bound) {
        int r;
        if (bound == recodedList.size()) throw new  IllegalArgumentException("给定范围内已无随机数可用");
        while(recodedList.contains(r = super.nextInt(bound))){}
        recodedList.add(r);
        return r;
    }
}