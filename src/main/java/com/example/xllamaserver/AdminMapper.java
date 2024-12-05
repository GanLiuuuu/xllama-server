package com.example.xllamaserver;

import com.example.xllamaserver.DTO.BotCommentDTO;
import com.example.xllamaserver.DTO.BotDTO;
import com.example.xllamaserver.DTO.UserDTO;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface AdminMapper {

    @Select("""
        SELECT u.user_id AS userId, u.username, b.id AS botId, b.name AS botName, r.content AS comment, r.rating AS ranking, r.date AS commentTime FROM Reviews r JOIN Bot b ON r.bot = b.id JOIN User u ON r.user = u.email
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
        id, name, views, description, isOfficial, price, state, createdBy, createdAt
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
        isOfficial = #{isOfficial},
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

    @Update("UPDATE User SET freeTokens = 100 WHERE userType = 'regular'")
    void resetFreeTokens();

    @Update("UPDATE Bot SET isOfficial = true WHERE id = #{botId}")
    void updateIsOfficial(int botId);

    @Update("UPDATE Bot SET isOfficial = false WHERE id != #{botId}")
    void updateIsNotOfficial(int botId);

    @Select("""
    SELECT 
        id, name, views, description, isOfficial, avatarUrl
    FROM Bot where isOfficial = true;
""")
    Map<String, String> getOfficial();
}