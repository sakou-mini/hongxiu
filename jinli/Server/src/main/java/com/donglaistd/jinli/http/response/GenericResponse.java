package com.donglaistd.jinli.http.response;


import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by pengq on 2019/9/5 18:01.
 */
public abstract class GenericResponse implements RestResponse {
    private boolean isFieldsSet = false;
    private boolean isDataSet = false;
    private Map<String, Object> fields;
    private Object data;
    private int code;
    private String message;

    public GenericResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public GenericResponse appendField(String field, Object value) {
        if (!this.isFieldsSet) {
            this.isFieldsSet = true;
            this.fields = new HashMap<>();
        }

        this.fields.put(field, value);
        return this;
    }

    public GenericResponse withData(Object data) {
        if (!this.isDataSet) {
            this.isDataSet = true;
        }

        this.data = data;
        return this;
    }

    public Optional<Map<String, Object>> getFields() {
        return this.isFieldsSet ? Optional.of(this.fields) : Optional.empty();
    }

    public void setFields(Map<String, Object> fields) {
        this.fields = fields;
    }

    public Optional<Object> getData() {
        return this.isDataSet ? Optional.ofNullable(this.data) : Optional.empty();
    }

    public void setData(Object data) {
        this.data = data;
    }

    public boolean isFieldsSet() {
        return isFieldsSet;
    }

    public void setFieldsSet(boolean fieldsSet) {
        isFieldsSet = fieldsSet;
    }

    public boolean isDataSet() {
        return isDataSet;
    }

    public void setDataSet(boolean dataSet) {
        isDataSet = dataSet;
    }

    @Override
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}