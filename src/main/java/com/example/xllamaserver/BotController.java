package com.example.xllamaserver;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bots")
public class BotController {
    BotMapper botMapper;
    @PostMapping("/add")
    public String insertBot(@RequestBody Bot bot) {

        botMapper.insertBot(bot);
        return "User registered successfully";
    }

}
