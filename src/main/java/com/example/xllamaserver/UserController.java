package com.example.xllamaserver;

import com.alibaba.fastjson2.JSONObject;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;
import com.alibaba.fastjson2.JSONObject;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

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
    private static final String AVATAR_DIR = "/Users/zhuyuhao/Desktop/OOAD/V1/xllama-server/src/main/resources/static/avatars/";
    private static final String COVER_PHOTO_DIR = "/Users/zhuyuhao/Desktop/OOAD/V1/xllama-server/src/main/resources/static/coverPhoto/";
    private static final String BASE_URL = "http://localhost:8081/";

    @GetMapping("/getAll")
    public List<User> getAllUser() {
        System.out.println("bbbbb");
        return userMapper.getAllUser();
    }
    @GetMapping("/getUserId")
    public ResponseEntity<?> getUserIdByEmail(@RequestParam String email) {
        try {
            System.out.println(email);
            Integer userId = userMapper.getUserIdByEmail(email);
            System.out.println(userId);
            if (userId != null) {
                return ResponseEntity.ok().body(Map.of("userId", userId));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error getting user ID");
        }
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
            File tempFile = File.createTempFile("avatar_", ".tmp");
            file.transferTo(tempFile);  // 将上传的文件保存到临时文件中

            // 使用 sm.ms 上传文件
            HttpResponse<String> response = Unirest.post("https://smms.app/api/v2/upload")
                    .header("Authorization", "xUYYZYpzzZFXNRoCiuy1OGjc7nGlgaIL") // 替换为你的 sm.ms API token
                    .field("smfile", tempFile)
                    .asString();

            // 解析上传响应
            String responseBody = response.getBody();
            JSONObject jsonResponse = JSONObject.parseObject(responseBody);
            String imageUrl = null;

            // 如果图片重复，获取重复图片的 URL
            if ("image_repeated".equals(jsonResponse.getString("code"))) {
                imageUrl = jsonResponse.getString("images");
            } else {
                imageUrl = JSONObject.parseObject(jsonResponse.getString("data")).getString("url");
            }
            System.out.println(1);
            System.out.println(1);
            System.out.println(1);
            System.out.println(imageUrl);


            // 删除临时文件
            tempFile.delete();
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

    @PostMapping("/setInfo")
    public ResponseEntity<?> setUserInfo(@RequestParam("email") String email,
                                         @RequestParam(value = "username", required = false) String username,
                                         @RequestParam(value = "about", required = false) String about,
                                         @RequestParam(value = "emailAddress", required = false) String emailAddress,
                                         @RequestParam(value = "firstName", required = false) String firstName,
                                         @RequestParam(value = "lastName", required = false) String lastName,
                                         @RequestParam(value = "country", required = false) String country,
                                         @RequestParam(value = "photo", required = false) MultipartFile photo,
                                         @RequestParam(value = "coverPhoto", required = false) MultipartFile coverPhoto) {

        try {
            if (username != null && !username.isEmpty()) userMapper.setUsername(username, email);
            if (about != null && !about.isEmpty()) userMapper.setAbout(about, email);
            if (firstName != null && !firstName.isEmpty()) userMapper.setFirstname(firstName, email);
            if (lastName != null && !lastName.isEmpty()) userMapper.setLastname(lastName, email);
            if (country != null && !country.isEmpty()) userMapper.setCountry(country, email);

            if (photo != null && !photo.isEmpty()) {
                String avatarUrl = uploadToSmms(photo);
                if (avatarUrl != null) {
                    userMapper.setAvatarUrl(avatarUrl, email);
                } else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload avatar to sm.ms");
                }
            }

            // 更新封面照片
            if (coverPhoto != null && !coverPhoto.isEmpty()) {
                String coverPhotoPath = uploadToSmms(coverPhoto);
                if (coverPhotoPath != null) {
                    userMapper.setCoverPhoto(coverPhotoPath, email);
                } else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload avatar to sm.ms");
                }
            }

            return ResponseEntity.ok("User information updated successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update user information.");
        }
    }



    private void saveFile(MultipartFile file, String filePath) throws IOException {
        File destination = new File(filePath);
        destination.getParentFile().mkdirs();
        file.transferTo(destination);
    }

    private String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }




    @GetMapping("/searchUsers")
    public ResponseEntity<?> searchUsersByUsername(@RequestParam("username") String username) {
        try {
            if (username == null) {
                return ResponseEntity.badRequest().body("Username parameter cannot be empty");
            }
            if (username.isEmpty()){
                List<Map<String, Object>> users = userMapper.getAllUsers();
                return ResponseEntity.ok(users);
            }

            List<Map<String, Object>> users = userMapper.getUserDetailsByUsername(username.trim());
            if (users.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No users found");
            }

            return ResponseEntity.ok(users);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to search users");
        }
    }

    @GetMapping("/details")
    public ResponseEntity<?> getUserDetailsByEmail(@RequestParam("email") String email) {
        try {
            // 校验 email 参数是否为空
            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Email parameter cannot be empty");
            }

            // 查询用户完整记录
            Map<String, Object> userDetails = userMapper.getCompleteUserDetailsByEmail(email.trim());
            if (userDetails == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            return ResponseEntity.ok(userDetails);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to fetch user details");
        }
    }

    @PostMapping("/writeComments")
    public ResponseEntity<?> writeComments(@RequestBody Map<String, Object> payload) {
        try {
            // 从请求体中提取参数
            String comment = (String) payload.get("comment");
            int rating = Integer.parseInt(payload.get("rating").toString());
            String email1 = (String) payload.get("email1");
            String email2 = (String) payload.get("email2");

            int profileId = userMapper.getUserIdByEmail(email1);
            int commenterId = userMapper.getUserIdByEmail(email2);
            userMapper.insertComment(profileId, commenterId, comment, rating);
            String messeage = email2 + ":\n" + comment + "\nranking:" + rating + "\n";

            try {
                // 尝试发送邮件
                sendEmail(email1, "Comments for your profile", messeage);
            } catch (Exception emailException) {
                // 捕获邮件发送失败的异常
                emailException.printStackTrace();
                System.err.println("Failed to send email, but the comment was saved successfully.");
            }


            return ResponseEntity.ok("Comment submitted successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to submit comment.");
        }
    }

    public static void sendEmail(String to, String subject, String body) {
        final String from = "3175023883@qq.com";
        final String password = "zkkhkngfqbjidggd";

        String host = "smtp.qq.com";
        int port = 587;

        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", String.valueOf(port));
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from)); // 发件人
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to)); // 收件人
            message.setSubject(subject); // 邮件主题
            message.setText(body); // 邮件正文

            Transport.send(message);
            System.out.println("邮件发送成功！");
        } catch (MessagingException e) {
            e.printStackTrace();
            System.err.println("邮件发送失败！");
        }
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


    @GetMapping("/getRecentUseBots")
    public ResponseEntity<?> getRecentUseBots(@RequestParam("email") String email) {
        System.out.println(7777);
        try {
            // 校验 email 参数是否为空
            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Email parameter cannot be empty");
            }
            int userId = userMapper.getUserIdByEmail(email);
            List<Map<String, Object>> users = userMapper.getRecentUseBots(userId);

            if (users == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            return ResponseEntity.ok(users);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to fetch user details");
        }
    }


}