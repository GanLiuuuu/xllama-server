package com.example.xllamaserver;

import org.apache.ibatis.annotations.*;
import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper {
    @Select("SELECT * FROM user1")
    List<User> getAllUser();

    @Insert("INSERT INTO user(username, email, password,userType) VALUES(#{username}, #{email}, #{password},'regular')")
    void insertUser(User user);

    // 在注册新账户时调用，检查是否一个邮箱重复注册
    // 在登录账户时使用，检查用户是否已注册
    @Select("SELECT COUNT(*) FROM user WHERE email = #{email}")
    int countByEmail(String email);

    // 登陆账户时调用，检查是否存在该用户是否密码正确
    @Select("SELECT COUNT(*) FROM user WHERE email = #{email} AND password = #{password}")
    int checkCount(String email, String password);

    @Select("SELECT * FROM user WHERE email = #{email}")
    User getUserByEmail(String email);

    @Update("UPDATE user SET points = points + #{points} WHERE email = #{email}")
    int rechargePoints(String email, int points);

    @Update("UPDATE user SET points = points - #{points}, tokens = tokens + (#{points} * 100) WHERE email = #{email} AND points >= #{points}")
    int redeemPoints(@Param("email") String email, @Param("points") int points);

    @Update("UPDATE user SET bio = #{bios} WHERE email = #{email}")
    int updateBio(@Param("email") String email, @Param("bios") String bios);

    @Update("UPDATE user SET username = #{username} WHERE email = #{email}")
    int updateName(@Param("email") String email, @Param("username") String username);

    @Update("UPDATE user SET avatarUrl = #{AvatarUrl} WHERE email = #{email}")
    int updateAvatarUrl(@Param("email") String email, @Param("AvatarUrl") String AvatarUrl);

    @Select("SELECT user_id FROM User WHERE email = #{email}")
    Integer getUserIdByEmail(String email);

    // 查询评论数据，包括评论者昵称
    @Select("""
        SELECT 
            u.username AS reviewerName,
            c.comment_text AS reviewText,
            c.rating AS rating,
            c.created_at AS reviewDate
        FROM 
            UserProfileComment c
        JOIN 
            User u ON c.commenter_id = u.user_id
        WHERE 
            c.profile_owner_id = #{profileOwnerId}
        ORDER BY 
            c.created_at DESC
    """)
    List<Map<String, Object>> getCommentsByUserId(Integer profileOwnerId);

    @Select("""
        SELECT 
            b.name AS botName, 
            b.description AS botDescription
        FROM 
            Bot b
        JOIN 
            User u ON b.createdBy = u.email
        WHERE 
            u.email = #{email}
    """)
    List<Map<String, String>> getBotsByEmail(String email);

    @Select("""
        SELECT 
            b.bot_name AS botName, 
            b.description AS botDescription
        FROM 
            Bot b
    """)
    List<Map<String, String>> getAllBots();

    // 获取用户和 bots 的交互统计
    @Select("SELECT b.id, b.name, cs.interaction_count, cs.last_interaction " +
            "FROM ChatSummary cs " +
            "JOIN Bot b ON cs.bot_id = b.id " +
            "WHERE cs.user_id = #{userId}")
    List<Map<String, Object>> getUsageStats(int userId);


    @Update("UPDATE user SET username = #{username} WHERE email = #{email}")
    void setUsername(String username, String email);

    @Update("UPDATE user SET about = #{about} WHERE email = #{email}")
    void setAbout(String about, String email);

    @Update("UPDATE user SET username = #{username} WHERE email = #{email}")
    void setEmail(String emailAddress, String email);

    @Update("UPDATE user SET firstname = #{firstName} WHERE email = #{email}")
    void setFirstname(String firstName, String email);

    @Update("UPDATE user SET lastname = #{lastName} WHERE email = #{email}")
    void setLastname(String lastName, String email);

    @Update("UPDATE user SET country = #{country} WHERE email = #{email}")
    void setCountry(String country, String email);

    @Update("UPDATE user SET avatarUrl = #{s} WHERE email = #{email}")
    void setAvatarUrl(String s, String email);

    @Update("UPDATE user SET coverPhoto = #{s} WHERE email = #{email}")
    void setCoverPhoto(String s, String email);

    @Select("SELECT username, email, avatarUrl, bio FROM User WHERE username LIKE CONCAT('%', #{username}, '%')")
    List<Map<String, Object>> getUserDetailsByUsername(@Param("username") String username);

    @Select("SELECT * FROM User WHERE email = #{email}")
    Map<String, Object> getCompleteUserDetailsByEmail(@Param("email") String email);

    @Insert(" INSERT INTO UserProfileComment (profile_owner_id, commenter_id, comment_text, rating, created_at) VALUES (#{profileOwnerId}, #{commenterId}, #{commentText}, #{rating}, CURRENT_TIMESTAMP) ")
    void insertComment(Integer profileOwnerId, Integer commenterId, String commentText, Integer rating);

    @Select("SELECT * FROM User")
    List<Map<String, Object>> getAllUsers();

    @Select("SELECT s.bot_id as id, b.name as projectName, b.state as status,s.last_interaction as last_use_time,s.interaction_count as interactionCount from " +
            "Bot b join ChatSummary s on b.id = s.bot_id " +
            "where s.user_id = #{userId}")
    List<Map<String, Object>> getRecentUseBots(int userId);
}