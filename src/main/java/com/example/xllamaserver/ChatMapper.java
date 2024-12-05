package com.example.xllamaserver;

import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface ChatMapper {

    @Insert("INSERT INTO ChatSession (user_id, bot_id, session_name) VALUES (#{userId}, #{botId}, #{sessionName})")
    @Options(useGeneratedKeys = true, keyProperty = "sessionId")
    void createSession(ChatSession session);

    @Insert("INSERT INTO ChatInteraction (session_id, user_id, bot_id, interaction_req, interaction_res) " +
            "VALUES (#{sessionId}, #{userId}, #{botId}, #{interactionReq}, #{interactionRes})")
    void saveInteraction(ChatInteraction interaction);
    @Select("SELECT * FROM ChatInteraction WHERE session_id = #{sessionId} ORDER BY interaction_time ASC")
    List<ChatInteraction> getChatHistory(Integer sessionId);

    @Select("SELECT * FROM ChatInteraction WHERE session_id = #{sessionId} ORDER BY interaction_time ASC")
    List<Map<String, Object>> getSessionHistory(@Param("sessionId") Integer sessionId);
    @Delete("DELETE FROM ChatInteraction WHERE session_id = #{sessionId}")
    void deleteSessionHistory(@Param("sessionId") Integer sessionId);

    @Select("SELECT price FROM Bot WHERE id = #{botId}")
    Float getBotPrice(Integer botId);

    @Select("SELECT tokens FROM User WHERE user_id = #{userId}")
    Integer getUserTokens(Integer userId);

    @Update("UPDATE User SET tokens = tokens - #{tokens} WHERE user_id = #{userId} AND tokens >= #{tokens}")
    int deductUserTokens(@Param("userId") Integer userId, @Param("tokens") Float tokens);

    @Select("SELECT session_id as sessionId, session_name as sessionName, created_at as createdAt " +
            "FROM ChatSession " +
            "WHERE user_id = #{userId} " +
            "ORDER BY created_at DESC")
    List<Map<String, Object>> getUserSessions(@Param("userId") Integer userId);

    @Select("SELECT freeTokens FROM User WHERE user_id = #{userId}")
    Integer getUserFreeTokens(Integer userId);

    @Update("UPDATE User SET freeTokens = freeTokens - #{tokens} WHERE user_id = #{userId} AND freeTokens >= #{tokens}")
    int deductUserFreeTokens(@Param("userId") Integer userId, @Param("tokens") Float tokens);

    @Select("SELECT isOfficial FROM Bot WHERE id = #{botId}")
    Boolean isOfficialBot(Integer botId);
}