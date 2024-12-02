package com.example.xllamaserver;

import org.apache.ibatis.annotations.*;

@Mapper
public interface ChatMapper {

    @Insert("INSERT INTO ChatSession (user_id, bot_id, session_name) VALUES (#{userId}, #{botId}, #{sessionName})")
    @Options(useGeneratedKeys = true, keyProperty = "sessionId")
    void createSession(ChatSession session);

    @Insert("INSERT INTO ChatInteraction (session_id, user_id, bot_id, interaction_req, interaction_res) " +
            "VALUES (#{sessionId}, #{userId}, #{botId}, #{interactionReq}, #{interactionRes})")
    void saveInteraction(ChatInteraction interaction);
}