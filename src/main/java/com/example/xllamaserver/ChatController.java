package com.example.xllamaserver;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}