package com.example.xllamaserver;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ChatInteraction {
    private Integer interactionId;
    private Integer sessionId;
    private Integer userId;
    private Integer botId;
    private String interactionReq;
    private String interactionRes;
    private LocalDateTime interactionTime;
}