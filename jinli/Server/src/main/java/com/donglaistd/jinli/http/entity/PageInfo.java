package com.donglaistd.jinli.http.entity;

import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.donglaistd.jinli.constant.GameConstant.HTTP_RESULT_SUCCESS;

public class PageInfo<T> {
    public Collection<T> content;
    public Pageable pageable;
    public long total;
    public Map<String, Object> otherParam = new HashMap<>();
    public int resultCode = HTTP_RESULT_SUCCESS;

    public PageInfo(Collection<T> content, Pageable pageable, long total) {
        this.content = content;
        this.pageable = pageable;
        this.total = total;
    }

    public PageInfo(Collection<T> content, long total) {
        this.content = content;
        this.total = total;
    }

    public PageInfo(Collection<T> content, long total,int resultCode) {
        this.content = content;
        this.total = total;
        this.resultCode = resultCode;
    }

    public PageInfo() {
    }

    public Collection<T> getContent() {
        return content;
    }

    public Pageable getPageable() {
        return pageable;
    }

    public long getTotal() {
        return total;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void addOtherParam(String key, Object value){
        otherParam.put(key, value);
    }
    public Map<String, Object> getOtherParam() {
        return otherParam;
    }
}
