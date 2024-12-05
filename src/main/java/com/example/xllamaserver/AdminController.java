package com.example.xllamaserver;

import com.example.xllamaserver.DTO.BotCommentDTO;
import com.example.xllamaserver.DTO.BotDTO;
import com.example.xllamaserver.DTO.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.util.Map;


@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AdminMapper adminmapper;

    @GetMapping("/getOfficial")
    public ResponseEntity<?> getUserIdByEmail() {
        try {
            Map<String, String> officialBot = adminmapper.getOfficial();
            if (officialBot == null || officialBot.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No official bot found");
            }
            return ResponseEntity.ok(officialBot);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error getting user ID");
        }
    }

    @PostMapping("/resetFreeTokens")
    public ResponseEntity<String> resetFreeTokens(@RequestParam("reset") boolean reset) {
        try {
            // 调用 AdminMapper 更新 Bot 的价格
            adminmapper.resetFreeTokens();
            System.out.println(666);
            return ResponseEntity.ok("Price updated successfully");
        } catch (Exception e) {
            // 处理异常
            return ResponseEntity.status(500).body("Failed to update price: " + e.getMessage());
        }
    }

    @PostMapping("/bot/changePrice")
    public ResponseEntity<String> changePrice(@RequestParam("bot") int botId, @RequestParam("price") float price) {
        try {
            // 调用 AdminMapper 更新 Bot 的价格
            adminmapper.updateBotPrice(botId, price);
            return ResponseEntity.ok("Price updated successfully");
        } catch (Exception e) {
            // 处理异常
            return ResponseEntity.status(500).body("Failed to update price: " + e.getMessage());
        }
    }

    @PostMapping("/setCustom")
    public ResponseEntity<String> setCustom(@RequestParam("bot") int botId) {
        try {
            // 调用 AdminMapper 更新 Bot 的价格
            adminmapper.updateIsOfficial(botId);
            adminmapper.updateIsNotOfficial(botId);
            return ResponseEntity.ok("Price updated successfully");
        } catch (Exception e) {
            // 处理异常
            return ResponseEntity.status(500).body("Failed to update price: " + e.getMessage());
        }
    }

    @PostMapping("/bot/passAudit")
    public ResponseEntity<String> passAudit(@RequestParam("bot") int botId) {
        try {
            // 调用 AdminMapper 更新 Bot 的价格
            adminmapper.passAudit(botId);
            return ResponseEntity.ok("Price updated successfully");
        } catch (Exception e) {
            // 处理异常
            return ResponseEntity.status(500).body("Failed to update price: " + e.getMessage());
        }
    }

    @PostMapping("/bot/failAudit")
    public ResponseEntity<String> fialAudit(@RequestParam("bot") int botId) {
        try {
            // 调用 AdminMapper 更新 Bot 的价格
            adminmapper.failAudit(botId);
            return ResponseEntity.ok("Price updated successfully");
        } catch (Exception e) {
            // 处理异常
            return ResponseEntity.status(500).body("Failed to update price: " + e.getMessage());
        }
    }

    @GetMapping("/export/comments")
    public ResponseEntity<byte[]> exportComments() {
        try {
            // 获取数据
            List<BotCommentDTO> comments = adminmapper.getCommentDetails();

            // 创建 Excel 文件
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Comments");

            // 设置标题行
            Row header = sheet.createRow(0);
            String[] columns = {"user_id", "username", "bot_id", "botname", "comment", "ranking", "comment_time"};
            for (int i = 0; i < columns.length; i++) {
                header.createCell(i).setCellValue(columns[i]);
            }

            // 填充数据
            int rowIdx = 1;
            for (BotCommentDTO comment : comments) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(comment.getUserId());
                row.createCell(1).setCellValue(comment.getUsername());
                row.createCell(2).setCellValue(comment.getBotId());
                row.createCell(3).setCellValue(comment.getBotName());
                row.createCell(4).setCellValue(comment.getComment());
                row.createCell(5).setCellValue(comment.getRanking());
                row.createCell(6).setCellValue(comment.getCommentTime().toString());
            }

            // 将 Excel 写入字节流
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();

            // 返回文件
            byte[] excelBytes = outputStream.toByteArray();
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=comments.xlsx");
            return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/export/stats")
    public ResponseEntity<byte[]> exportStats() {
        try {
            List<UserDTO> users = adminmapper.getAllUsers();
            List<BotDTO> bots = adminmapper.getAllBots();

            Workbook workbook = new XSSFWorkbook();

            Sheet userSheet = workbook.createSheet("Users");
            String[] userColumns = {"user_id", "username", "email", "userType", "points", "tokens"};
            Row userHeader = userSheet.createRow(0);
            for (int i = 0; i < userColumns.length; i++) {
                userHeader.createCell(i).setCellValue(userColumns[i]);
            }
            int userRowIdx = 1;
            for (UserDTO user : users) {
                Row row = userSheet.createRow(userRowIdx++);
                row.createCell(0).setCellValue(user.getUserId());
                row.createCell(1).setCellValue(user.getUsername());
                row.createCell(2).setCellValue(user.getEmail());
                row.createCell(3).setCellValue(user.getUserType());
                row.createCell(4).setCellValue(user.getPoints());
                row.createCell(5).setCellValue(user.getTokens());
            }

            Sheet botSheet = workbook.createSheet("Bots");
            String[] botColumns = {"id", "name", "views", "description", "is_official", "price", "state"};
            Row botHeader = botSheet.createRow(0);
            for (int i = 0; i < botColumns.length; i++) {
                botHeader.createCell(i).setCellValue(botColumns[i]);
            }
            int botRowIdx = 1;
            for (BotDTO bot : bots) {
                Row row = botSheet.createRow(botRowIdx++);
                row.createCell(0).setCellValue(bot.getId());
                row.createCell(1).setCellValue(bot.getName());
                row.createCell(2).setCellValue(bot.getViews());
                row.createCell(3).setCellValue(bot.getDescription());
                row.createCell(4).setCellValue(bot.getIsOfficial() ? "Yes" : "No");
                row.createCell(5).setCellValue(bot.getPrice());
                row.createCell(6).setCellValue(bot.getState());
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();

            byte[] excelBytes = outputStream.toByteArray();
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=users_and_bots.xlsx");
            return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/upload/excel")
    public ResponseEntity<String> uploadExcel(@RequestParam("file") MultipartFile file) {
        try {
            System.out.println(777);
            // 确保文件为 Excel 格式
            if (!file.getOriginalFilename().endsWith(".xlsx")) {
                return ResponseEntity.badRequest().body("Invalid file type. Please upload an Excel file.");
            }

            // 解析 Excel 文件
            Workbook workbook = new XSSFWorkbook(file.getInputStream());

            // 读取 Users Sheet
            Sheet userSheet = workbook.getSheet("Users");
            if (userSheet != null) {
                for (int i = 1; i <= userSheet.getLastRowNum(); i++) { // 跳过标题行
                    Row row = userSheet.getRow(i);
                    if (row == null) continue;

                    int userId = (int) row.getCell(0).getNumericCellValue();
                    String username = row.getCell(1).getStringCellValue();
                    String email = row.getCell(2).getStringCellValue();
                    String userType = row.getCell(3).getStringCellValue();
                    int points = (int) row.getCell(4).getNumericCellValue();
                    int tokens = (int) row.getCell(5).getNumericCellValue();

                    // 更新数据库
                    adminmapper.updateUser(userId, username, email, userType, points, tokens);
                }
            }

            // 读取 Bots Sheet
            Sheet botSheet = workbook.getSheet("Bots");
            if (botSheet != null) {
                for (int i = 1; i <= botSheet.getLastRowNum(); i++) { // 跳过标题行
                    Row row = botSheet.getRow(i);
                    if (row == null) continue;

                    int botId = (int) row.getCell(0).getNumericCellValue();
                    String name = row.getCell(1).getStringCellValue();
                    int views = (int) row.getCell(2).getNumericCellValue();
                    String description = row.getCell(3).getStringCellValue();
                    boolean isOfficial = "Yes".equalsIgnoreCase(row.getCell(4).getStringCellValue());
                    float price = (float) row.getCell(5).getNumericCellValue();
                    String state = row.getCell(6).getStringCellValue();

                    // 更新数据库
                    adminmapper.updateBot(botId, name, views, description, isOfficial, price, state);
                }
            }

            workbook.close();
            return ResponseEntity.ok("Excel file processed and database updated successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing the file.");
        }
    }



}