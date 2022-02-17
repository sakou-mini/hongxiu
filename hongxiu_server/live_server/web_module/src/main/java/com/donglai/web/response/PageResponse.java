package com.donglai.web.response;

import lombok.Data;

import java.util.List;


/**
 * Created By pengq On 2020/5/12 21:36
 */

@Data
public class PageResponse {
    private int pageNum;
    private int pageSize;
    private int totalPage;
    private Long total;
    private List<?> list;
    private int code = GlobalResponseCode.SUCCESS.getCode();

    public static PageResponse build(PageInfo<?> pageData, boolean includeData) {
        PageResponse response = new PageResponse();
        response.setPageNum(pageData.getPageNum());
        response.setPageSize(pageData.getPageSize());
        response.setTotal(pageData.getTotal());
        if (includeData) {
            response.setList(pageData.getContent());
        }
        return response;
    }

    public static PageResponse build(PageInfo<?> pageData) {
        return build(pageData, true);
    }

    public static PageResponse build(int code) {
        PageResponse response = new PageResponse();
        response.setCode(code);
        return response;
    }
}

