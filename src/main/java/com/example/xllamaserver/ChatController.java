package com.example.xllamaserver;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatMapper chatMapper;

    @PostMapping("/session")
    public ResponseEntity<?> createSession(@RequestBody ChatSession chatSession) {
        try {
            System.out.println(chatSession);
            chatMapper.createSession(chatSession);
            return ResponseEntity.ok().body(Map.of("sessionId", chatSession.getSessionId()));
        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.badRequest().body("Failed to create chat session");
        }
    }

    @PostMapping("/interaction")
    public ResponseEntity<?> saveInteraction(@RequestBody ChatInteraction interaction) {
        try {
            // 1. 获取 bot 的价格和类型
            Float botPrice = chatMapper.getBotPrice(interaction.getBotId());
            Boolean isOfficial = chatMapper.isOfficialBot(interaction.getBotId());
            
            if (botPrice == null || isOfficial == null) {
                return ResponseEntity.badRequest().body("Bot not found");
            }

            // 2. 根据bot类型选择扣除不同的tokens
            if (isOfficial) {
                // 使用免费tokens
                Integer userFreeTokens = chatMapper.getUserFreeTokens(interaction.getUserId());
                if (userFreeTokens == null) {
                    return ResponseEntity.badRequest().body("User not found");
                }

                if (userFreeTokens < botPrice) {
                    return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED)
                            .body("Insufficient free tokens");
                }

                // 扣除免费tokens
                int updateResult = chatMapper.deductUserFreeTokens(interaction.getUserId(), botPrice);
                if (updateResult != 1) {
                    return ResponseEntity.badRequest().body("Failed to deduct free tokens");
                }
            } else {
                // 使用付费tokens
                Integer userTokens = chatMapper.getUserTokens(interaction.getUserId());
                if (userTokens == null) {
                    return ResponseEntity.badRequest().body("User not found");
                }

                if (userTokens < botPrice) {
                    return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED)
                            .body("Insufficient tokens");
                }

                // 扣除付费tokens
                int updateResult = chatMapper.deductUserTokens(interaction.getUserId(), botPrice);
                if (updateResult != 1) {
                    return ResponseEntity.badRequest().body("Failed to deduct tokens");
                }
            }

            // 4. 保存交互记录
            chatMapper.saveInteraction(interaction);
            
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.badRequest().body("Failed to save chat interaction");
        }
    }
    @GetMapping("/history/{sessionId}")
    public ResponseEntity<?> getChatHistory(@PathVariable Integer sessionId) {
        try {
            List<ChatInteraction> history = chatMapper.getChatHistory(sessionId);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to get chat history");
        }
    }


    @GetMapping("/session/{sessionId}/history")
    public ResponseEntity<?> getSessionHistory(@PathVariable Integer sessionId) {
        try {
            List<Map<String, Object>> history = chatMapper.getSessionHistory(sessionId);
            System.out.println(history);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.badRequest().body("Failed to get chat history");
        }
    }
    @DeleteMapping("/{sessionId}/history")
    public ResponseEntity<?> clearSessionHistory(@PathVariable Integer sessionId) {
        try {
            chatMapper.deleteSessionHistory(sessionId);
            System.out.println("deleted");
            return ResponseEntity.ok()
                    .body(Map.of("message", "Chat history cleared successfully"));
        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Failed to clear chat history"));
        }
    }

    @GetMapping("/sessions")
    public ResponseEntity<?> getUserSessions(@RequestParam Integer userId) {
        try {
            List<Map<String, Object>> sessions = chatMapper.getUserSessions(userId);
            return ResponseEntity.ok(sessions);
        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.badRequest().body("Failed to get user sessions");
        }
    }
}