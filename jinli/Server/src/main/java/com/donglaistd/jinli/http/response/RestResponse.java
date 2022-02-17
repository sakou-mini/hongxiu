package com.donglaistd.jinli.http.response;

import com.donglaistd.jinli.http.response.serializer.JsonDeserializer;
import com.donglaistd.jinli.http.response.serializer.JsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Map;
import java.util.Optional;

@JsonDeserialize(using = JsonDeserializer.class)
@JsonSerialize(using = JsonSerializer.class)
public interface RestResponse<T> extends IErrorCode {
    Optional<Map<String, Object>> getFields();
    Optional<T> getData();
}
