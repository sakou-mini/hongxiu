package com.donglai.web.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
public class PageInfo<T> implements Serializable {
    private int pageNum;
    private int pageSize;
    private long total;
    private List<T> content;


    public PageInfo(PageInfo pageInfo, List<T> content) {
        this.pageNum = pageInfo.getPageNum();
        this.pageSize = pageInfo.getPageSize();
        this.total = pageInfo.getTotal();
        this.content = content;
    }

    public PageInfo(int pageNum, int pageSize, long total, List<T> content) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.total = total;
        this.content = content;
    }

    public PageInfo(Pageable pageable, Page<T> page ,int totalNum) {
        this.pageNum = pageable.getPageNumber();
        this.pageSize = pageable.getPageSize();
        this.total = totalNum;
        this.content = page.getContent();
    }

    public PageInfo(Pageable pageable, List<T> content, long totalNum) {
        this.pageNum = pageable.getPageNumber();
        this.pageSize = pageable.getPageSize();
        this.total = totalNum;
        this.content = content;
    }
}
