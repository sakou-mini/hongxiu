package com.donglai.web.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
public class PageInfo<T> implements Serializable {
    private int pageNum;
    private int pageSize;
    private Long total;
    private List<T> content;

    public PageInfo(Long total, List<T> content) {
        this.total = total;
        this.content = content;
    }

    public PageInfo(int pageNum, int pageSize, Long total, List<T> content) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.total = total;
        this.content = content;
    }

    public PageInfo(Pageable pageable, List<T> content, long totalNum) {
        this.pageNum = pageable.getPageNumber();
        this.pageSize = pageable.getPageSize();
        this.total = totalNum;
        this.content = content;
    }
}
