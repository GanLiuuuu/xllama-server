package com.example.xllamaserver;


import lombok.Data;

@Data
public class User {

    private Integer Id;          // 用户ID
    private String username;          // 用户名
    private String password;          // 密码
    private String email;             // 邮箱
    private String userType;          // 用户类型
    private String avatarUrl;         // 头像URL
    private String bio;               // 自我介绍
    private Integer points;           // 积分数
    private Integer tokens;           // tokens

}


