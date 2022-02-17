package com.donglaistd.jinli.util;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

public class ListsUtil {
    //combination the list element
    public static <T> List<List<T>> combinations(List<T> list, int k) {
        if (k == 0 || list.isEmpty()) {
            return Collections.emptyList();
        }
        if (k == 1) {
            return list.stream().map(e -> Stream.of(e).collect(toList())).collect(toList());
        }
        //recursion
        Map<Boolean, List<T>> headAndTail = split(list, 1);
        List<T> head = headAndTail.get(true);
        List<T> tail = headAndTail.get(false);
        List<List<T>> c1 = combinations(tail, (k - 1)).stream().map(e -> {
            List<T> l = new ArrayList<>();
            l.addAll(head);
            l.addAll(e);
            return l;
        }).collect(toList());
        List<List<T>> c2 = combinations(tail, k);
        c1.addAll(c2);
        return c1;
    }

    public static <T> Map<Boolean, List<T>> split(List<T> list, int n) {
        return IntStream
                .range(0, list.size())
                .mapToObj(i -> new AbstractMap.SimpleEntry<>(i, list.get(i)))
                .collect(partitioningBy(entry -> entry.getKey() < n, mapping(AbstractMap.SimpleEntry::getValue, toList())));
    }
}
