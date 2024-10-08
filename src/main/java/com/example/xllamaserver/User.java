package com.example.xllamaserver;


import lombok.Data;

@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String bio;
}

