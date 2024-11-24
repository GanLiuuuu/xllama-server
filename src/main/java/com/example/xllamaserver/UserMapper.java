package com.example.xllamaserver;

import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper {
    @Select("SELECT * FROM user1")
    List<User> getAllUser();

    @Insert("INSERT INTO user(username, email, password) VALUES(#{username}, #{email}, #{password})")
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

    @Update("UPDATE user SET avatar_url = #{AvatarUrl} WHERE email = #{email}")
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
            b.bot_name AS botName, 
            b.description AS botDescription
        FROM 
            Bot b
        JOIN 
            User u ON b.created_by = u.user_id
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
    @Select("SELECT b.bot_id, b.bot_name, cs.interaction_count, cs.last_interaction " +
            "FROM ChatSummary cs " +
            "JOIN Bot b ON cs.bot_id = b.bot_id " +
            "WHERE cs.user_id = #{userId}")
    List<Map<String, Object>> getUsageStats(int userId);
}