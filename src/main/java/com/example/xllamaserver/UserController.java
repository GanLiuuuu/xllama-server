package com.example.xllamaserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserMapper userMapper;

    @GetMapping("/getAll")
    public List<User> getAllUser() {
        System.out.println("bbbbb");
        return userMapper.getAllUser();
    }

    @PostMapping("/add")
    public String insertUser(@RequestBody User user) {
        int count = userMapper.countByEmail(user.getEmail());
        // 如果邮箱已存在，返回错误信息
        if (count > 0) {
            return "Email already registered";
        }
        // 邮箱不存在，插入用户数据
        userMapper.insertUser(user);
        return "User registered successfully";
    }

    @PostMapping("/login")
    public String login (@RequestBody User user) {
        int count = userMapper.countByEmail(user.getEmail());
        // 邮箱没有注册
        if (count <= 0) {
            return "Email is not registered";
        }
        count = userMapper.checkCount(user.getEmail(),user.getPassword());
        if (count == 1) {
            return "login successful";
        }else {
            return "login failed, error password";
        }
    }

    @PostMapping("/getInformation")
    public User getInformation (@RequestBody User user) {
        User retrievedUser = userMapper.getUserByEmail(user.getEmail());
        return retrievedUser;
    }

    @PostMapping("/recharge")
    public String recharge (@RequestParam String email, @RequestParam String points) {
        try {
            userMapper.rechargePoints(email,Integer.parseInt(points));
            return "recharge successful";
        }catch (Exception e){
            return "recharge failed";
        }
    }

    @PostMapping("/redeem")
    public String redeem (@RequestParam String email, @RequestParam String points) {
        try {
            userMapper.redeemPoints(email,Integer.parseInt(points));
            return "redeem successful";
        }catch (Exception e){
            return "redeem failed";
        }
    }

    @PostMapping("/updateBio")
    public String updateBio (@RequestParam String email, @RequestParam String bios) {
        try {
            userMapper.updateBio(email,bios);
            return "updateBio successful";
        }catch (Exception e){
            return "updateBio failed";
        }
    }

    @PostMapping("/updateName")
    public String updateName (@RequestParam String email, @RequestParam String username) {
        try {
            userMapper.updateName(email,username);
            return "updateName successful";
        }catch (Exception e){
            return "updateName failed";
        }
    }

    @PostMapping("/updateAvatar")
    public ResponseEntity<?> updateAvatar(@RequestParam("file") MultipartFile file, @RequestParam("email") String email) {
        try {
            String uploadDir = "/Users/zhuyuhao/Desktop/OOAD/xllama-server/src/main/resources/static/avatars/";
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs(); // 如果目录不存在，创建目录
            }

            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFileName = email + fileExtension;

            File destinationFile = new File(uploadDir + newFileName);
            file.transferTo(destinationFile); // 如果存在同名文件会直接覆盖

            String AvatarUrl = "http://localhost:8081/avatars/" + newFileName;
            userMapper.updateAvatarUrl(email, AvatarUrl);
            return ResponseEntity.ok(Collections.singletonMap("url", AvatarUrl));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update avatar");
        }
    }

    @GetMapping("/comments")
    public ResponseEntity<?> getUserComments(@RequestParam("email") String email) {
        try {
            // 根据邮箱查询用户ID
            Integer profileOwnerId = userMapper.getUserIdByEmail(email);
            if (profileOwnerId == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            // 查询评论数据
            List<Map<String, Object>> comments = userMapper.getCommentsByUserId(profileOwnerId);
            return ResponseEntity.ok(comments);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to fetch comments");
        }
    }

    @GetMapping("/bots")
    public ResponseEntity<?> getUserBots(@RequestParam("email") String email) {
        try {
            List<Map<String, String>> bots = userMapper.getBotsByEmail(email);
            return ResponseEntity.ok(bots);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to fetch bots");
        }
    }

    @PostMapping("/getUsageStats")
    public ResponseEntity<?> getUsageStats(@RequestParam String email) {
        try {
            int userId = userMapper.getUserIdByEmail(email);
            // 获取用户和 bots 的交互统计
            List<Map<String, Object>> usageStats = userMapper.getUsageStats(userId);

            return ResponseEntity.ok(usageStats);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to fetch usage stats");
        }
    }



}