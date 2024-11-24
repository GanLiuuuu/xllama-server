package com.example.xllamaserver;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bots")
public class BotController {
    BotMapper botMapper;
    @PostMapping("/add")
    public String insertBot(@RequestBody Bot bot) {
        if(botMapper.ifexist(bot.getName(),bot.getVersion(),bot.getCreated_by()))
            return "bot already existed";

        try{
            botMapper.insertBot(bot);
            return "Bot uploaded successfully";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/showall")
    public List<Bot> showallbots(){
        try{
            return botMapper.getAllBots();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/info")
    public List<Bot> showuserbot(int user){
        try {
            return botMapper.selectbyauthor(user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/paid")
    public String Paid(int bot,int user){
        try {
            botMapper.insertpaid(true,user,bot);
            return "Paid successfully";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/ifpaid")
    public Boolean ifpaid(int bot,int user){
        try {
            return botMapper.ifpaid(user, bot);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
