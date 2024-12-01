package com.example.xllamaserver;

import java.sql.Timestamp;
import lombok.Data;

@Data
public class Bot {
    private Integer id;          // 用户ID
    private Integer views;
    private String name;          // bot名
    private String description;
    private String imgSrc;//缩略图路径
    private String avatarUrl;          // 头像
    private Float price;
    private String version;
    private boolean isOfficial;
    private String state;
    private String highlight;
    private String createdBy;
    private Timestamp createdAt;
    private Float rating;

}
