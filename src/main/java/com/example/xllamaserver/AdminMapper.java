package com.example.xllamaserver;

import com.example.xllamaserver.DTO.BotCommentDTO;
import com.example.xllamaserver.DTO.BotDTO;
import com.example.xllamaserver.DTO.UserDTO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AdminMapper {

    @Select("""
        SELECT 
            u.user_id AS userId, u.username, 
            b.id AS botId, b.name AS botName, 
            c.comment_text AS comment, c.rating AS ranking, 
            c.created_at AS commentTime
        FROM BotComment c
        JOIN Bot b ON c.bot_id = b.id
        JOIN User u ON c.commenter_id = u.user_id
    """)
    List<BotCommentDTO> getCommentDetails();

    @Select("""
    SELECT 
        user_id, username, email, userType, points, tokens, created_at, updated_at
    FROM User
""")
    List<UserDTO> getAllUsers();

    @Select("""
    SELECT 
        id, name, views, description, is_official, price, state, createdBy, createdAt
    FROM Bot
""")
    List<BotDTO> getAllBots();

    @Update("""
    UPDATE User
    SET username = #{username},
        email = #{email},
        userType = #{userType},
        points = #{points},
        tokens = #{tokens}
    WHERE user_id = #{userId}
""")
    void updateUser(int userId, String username, String email, String userType, int points, int tokens);

    @Update("""
    UPDATE Bot
    SET name = #{name},
        views = #{views},
        description = #{description},
        is_official = #{isOfficial},
        price = #{price},
        state = #{state}
    WHERE id = #{botId}
""")
    void updateBot(int botId, String name, int views, String description, boolean isOfficial, float price, String state);

    @Update("UPDATE Bot SET price = #{price} WHERE id = #{botId}")
    void updateBotPrice(int botId, float price);

    @Update("UPDATE Bot SET state = 'Online' WHERE id = #{botId}")
    void passAudit(int botId);

    @Delete("delete from Bot where id = #{botId}")
    void failAudit(int botId);
}