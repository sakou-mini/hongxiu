package com.donglaistd.jinli.http.response;

import java.util.List;


/**
 * Created By pengq On 2020/5/12 21:36
*/

public class PageResponse {
    private Long pageNum;
    private Long pageSize;
    private Long totalPage;
    private Long total;
    private List<?> list;
    private int code = GlobalResponseCode.SUCCESS.getCode();

    public static PageResponse build (PageInfo<?> pageData, boolean includeData){
        PageResponse response = new PageResponse();
        response.setPageNum(pageData.getPageNum());
        response.setPageSize(pageData.getPageSize());
        response.setTotal(pageData.getTotal());
        if (includeData){
            response.setList(pageData.getContent());
        }
        return response;
    }

    public static PageResponse build (PageInfo<?> pageData){
        return build(pageData,true);
    }

    public static PageResponse build (int code){
        PageResponse response = new PageResponse();
        response.setCode(code);
        return response;
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

    public Long getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Long totalPage) {
        this.totalPage = totalPage;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<?> getList() {
        return list;
    }

    public void setList(List<?> list) {
        this.list = list;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}

