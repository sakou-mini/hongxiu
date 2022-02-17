package com.donglai.web.process;

import com.donglai.web.WebBaseTest;
import com.donglai.web.response.PageInfo;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ListPageTest extends WebBaseTest {


    @Test
    public void test(){
        List<String> list = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            list.add(String.valueOf(i));
        }
        int page = 2;
        int size = 4;
        List<String> collect = list.stream().skip((page - 1) * size).limit(size).collect(Collectors.toList());

        PageInfo<String> curList = pageList(3, 10, list);
        List<String> subList = pageBySubList(list,1,2);

        System.out.println("page："+curList);
        System.out.println("pageBySubList:" + subList);
    }

    public static <T> PageInfo<T> pageList(int start, int limit, List<T> list) {
        List<T> safeList = Optional.ofNullable(list).orElseGet(Lists::newArrayList);
        int maxIndex = safeList.size();
        int nextIndex = start + limit;
        int fromIndex = Math.min(start, maxIndex);
        int toIndex = Math.min(nextIndex, maxIndex);
        String next = toIndex == maxIndex ? "0" : String.valueOf(nextIndex);
        return new PageInfo<>(limit,start,safeList.size(),safeList.subList(fromIndex, toIndex));
    }


    private List<String> pageBySubList(List<String> list, int currentPage, int pageSize) {
        int totalCount = list.size();
        int pagecount = 0;
        List<String> subList;
        int m = totalCount % pageSize;
        if (m > 0) {
            pagecount = totalCount / pageSize + 1;
        } else {
            pagecount = totalCount / pageSize;
        }
        if (m == 0) {
            int toIndex = pageSize * (currentPage);
            if(toIndex > totalCount) toIndex = totalCount;

            subList = list.subList((currentPage - 1) * pageSize, toIndex);

        } else {
            if (currentPage == pagecount) {
                subList = list.subList((currentPage - 1) * pageSize, totalCount);
            } else {
                subList = list.subList((currentPage - 1) * pageSize, pageSize * (currentPage));
            }
        }
        return subList;
    }
}
