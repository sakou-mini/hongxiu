package com.donglai.blogs.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class BlogsUserLikePost implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String userId;

    private Long postId;

    private Integer status;

}
