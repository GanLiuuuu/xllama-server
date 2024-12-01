package com.example.xllamaserver;

import java.sql.Timestamp;
import lombok.Data;

@Data
public class lastUseTime {
    private Integer id;          // 用户ID
    private Integer views;
    private String name;          // bot名
    private String description;
    private String imgSrc;//缩略图路径
    private String avatarUrl;          // 头像
    private Float price;
    private String version;
    private boolean is_official;
    private String state;
    private String highlight;
    private String created_by;
    private Timestamp created_at;
    private Timestamp lastTime;
    private Float rating;
}
