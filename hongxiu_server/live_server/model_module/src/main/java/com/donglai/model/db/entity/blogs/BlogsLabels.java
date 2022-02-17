package com.donglai.model.db.entity.blogs;

import com.donglai.common.annotation.AutoIncKey;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * @author Moon (动态标签中间表)
 * @date 2021-11-16 16:29
 */
@Document
@Data
@NoArgsConstructor
public class BlogsLabels {

    @AutoIncKey
    private long id;

    @Field
    @Indexed
    private long blogsId;

    @Field
    @Indexed
    private Integer labelsId;

}
