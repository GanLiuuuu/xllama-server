package com.example.xllamaserver;

import java.sql.Timestamp;
import lombok.Data;

@Data
public class Review {
    private String user;
    private int bot;
    private String content;
    private float rating;
    private int id;
    private Timestamp date;
    private String avatarUrl;
}
