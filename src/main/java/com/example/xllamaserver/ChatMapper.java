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
}