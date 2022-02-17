package com.donglaistd.jinli.http.entity;

import java.util.ArrayList;
import java.util.List;

public class HttpURLConnection<T> {

    public int code;

    public String msg;

    public List<T> data = new ArrayList<>(0);

    public int count;
    public String any;
    public void addData(T t){
        data.add(t);
    }
    public HttpURLConnection() {
    }

    public HttpURLConnection(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
