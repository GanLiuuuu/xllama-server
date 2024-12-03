package com.example.xllamaserver;


import org.springframework.beans.factory.annotation.Autowired;
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
}