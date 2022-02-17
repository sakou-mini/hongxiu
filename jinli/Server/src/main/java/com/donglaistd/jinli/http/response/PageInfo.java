package com.donglaistd.jinli.http.response;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.Serializable;
import java.util.List;

public class PageInfo<T> implements Serializable {
    private Long pageNum;
    private Long pageSize;
    private Long total;
    private List<T> content;

    public PageInfo(Long pageNum, Long pageSize, Long total, List<T> content) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.total = total;
        this.content = content;
    }

    public PageInfo(Pageable pageable, Page<T> page ,long totalNum) {
        this.pageNum = (long) pageable.getPageNumber();
        this.pageSize = (long) pageable.getPageSize();
        this.total = totalNum;
        this.content = page.getContent();
    }

    public PageInfo() {
    }

    public Long getPageNum() {
        return pageNum;
    }

    public void setPageNum(Long pageNum) {
        this.pageNum = pageNum;
    }

    public Long getPageSize() {
        return pageSize;
    }

    public void setPageSize(Long pageSize) {
        this.pageSize = pageSize;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }
}
