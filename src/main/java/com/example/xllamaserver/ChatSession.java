package com.example.xllamaserver;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ChatSession {
    private Integer sessionId;
    private Integer userId;
    private Integer botId;
    private String sessionName;
    private LocalDateTime createdAt;
    private LocalDateTime lastInteraction;
}
