package com.example.xllamaserver;

import com.alibaba.fastjson2.JSONObject;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.apache.ibatis.annotations.DeleteProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@RestController
@RequestMapping("/bots")
public class BotController {
    @Autowired
    private BotMapper botMapper;
    @PostMapping("/add")
    public String insertBot(@RequestPart("productDetails") Bot bot,@RequestPart("avatarFile")MultipartFile avatarFile, @RequestPart("botFile")MultipartFile botFile) {
        if(botMapper.ifExist(bot.getName(),bot.getVersion(),bot.getCreatedBy()))
            return "bot already existed";
            bot.setAvatarUrl(uploadToSmms(avatarFile));
        //TODO:trans botfile
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
            List<Bot> bots = botMapper.getAllBots();
            for(int i=0;i<bots.size();i++){
                bots.get(i).setRating(findavg(bots.get(i).getId()));
            }
            return bots;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/recommend")
    public List<Bot> recommendBots(String user){
        try{
            List<Bot> bots = botMapper.recommendBots(user);
            for(int i=0;i<bots.size();i++){
                bots.get(i).setRating(findavg(bots.get(i).getId()));
            }
            return bots;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @GetMapping("/userBots")
    public List<Bot> showuserbot(@RequestParam("id") String user){
        try {
            List<Bot> bots = botMapper.selectByAuthor(user);
            for(int i=0;i<bots.size();i++){
                bots.get(i).setRating(findavg(bots.get(i).getId()));
            }
            return bots;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/botInfo")
    public Bot showbot(@RequestParam("id") Integer bot){
        try {
            botMapper.updateViews(bot);
            Bot bot1 = botMapper.selectById(bot);
            bot1.setRating(findavg(bot));
            return bot1;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/avg")
    public float findavg(@RequestParam("id") Integer bot){
        try {
            return botMapper.ratingAvg(bot);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/updateLT")
    public String updateLastTime(@RequestParam("bot") Integer bot,@RequestParam("user") String user){
        try {
            if(botMapper.ifExistLT(user,bot))
                botMapper.updateLT(user,bot);
            else
                botMapper.insertLT(user,bot);
            return "Update successfully";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/botReviews")
    public List<Review> showReviews(@RequestParam("id") Integer bot){
        try {
            return botMapper.showreviews(bot);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/addReview")
    public String addReview(@RequestParam("user") String user, @RequestParam("bot") Integer bot, @RequestParam("rating") Float rating, @RequestParam("content") String content){
        try {
            botMapper.insertReviews(user, bot, content, rating);
            return "add review successfully!";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/addFAQ")
    public String addFAQ(@RequestParam("bot") Integer bot, @RequestParam("question") String question, @RequestParam("answer") String answer){
        try {
            botMapper.insertFAQs(bot, question, answer);
            return "add FAQ successfully!";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/botFAQs")
    public List<FAQ> showFAQs(@RequestParam("id") Integer bot){
        try {
            return botMapper.showFAQs(bot);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/recentUse")
    public List<lastUseTime> getRecentBot(@RequestParam("id") String user){
        try{
            List<lastUseTime> bots = botMapper.getRecent(user);
            for(int i=0;i<bots.size();i++){
                bots.get(i).setRating(findavg(bots.get(i).getId()));
            }
            return bots;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @GetMapping("/{email}")
    public ResponseEntity<List<Bot>> getUserBots(@PathVariable String email) {
        List<Bot> bots = botMapper.getBotsByUserEmail(email);
        return ResponseEntity.ok(bots);
    }

    @GetMapping("/ifSubscribe")
    public boolean ifUserBots(@RequestParam("botId") Integer bot,@RequestParam("email") String email) {
        try{
            return botMapper.ifUserBot(email,bot);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/{email}/{botId}")
    public ResponseEntity<Void> addUserBot(
            @PathVariable String email,
            @PathVariable Integer botId) {
        botMapper.addUserBot(email, botId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{email}/{botId}")
    public ResponseEntity<Void> removeUserBot(
            @PathVariable String email,
            @PathVariable Integer botId) {
        botMapper.removeUserBot(email, botId);
        return ResponseEntity.ok().build();
    }
    private String uploadToSmms(MultipartFile file) {
        try {
            File tempFile = File.createTempFile("avatar_", ".tmp");
            file.transferTo(tempFile);  // 将上传的文件保存到临时文件中

            // 使用 sm.ms 上传文件
            HttpResponse<String> response = Unirest.post("https://smms.app/api/v2/upload")
                    .header("Authorization", "xUYYZYpzzZFXNRoCiuy1OGjc7nGlgaIL") // 替换为你的 sm.ms API token
                    .field("smfile", tempFile)
                    .asString();

            String responseBody = response.getBody();
            JSONObject jsonResponse = JSONObject.parseObject(responseBody);
            String imageUrl = null;

            if ("image_repeated".equals(jsonResponse.getString("code"))) {
                imageUrl = jsonResponse.getString("images");
            } else {
                imageUrl = JSONObject.parseObject(jsonResponse.getString("data")).getString("url");
            }

            tempFile.delete();

            return imageUrl;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
