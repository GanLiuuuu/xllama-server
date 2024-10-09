package com.example.xllamaserver.component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.websocket.server.PathParam;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.stereotype.Component;

@ServerEndpoint("/chat")
@Component
@Slf4j
public class WebSocketServer {

    private static Map<String, WebSocketServer> SOCKET_MAP = new ConcurrentHashMap<>();

    @Getter
    private Session session;


    @OnOpen
    public void open(Session session, @PathParam("userId") String userId) {
        this.session = session;
        SOCKET_MAP.put(userId, this);
        log.info("用户{}连接成功,当前链接总人数{}", userId, SOCKET_MAP.size());
    }

    @OnClose
    public void close(Session session, @PathParam("userId") String userId) {
        SOCKET_MAP.remove(userId);
        log.info("用户{}断开连接,当前链接总人数{}", userId, SOCKET_MAP.size());
    }

    @OnMessage
    public void onMessage(String message, Session session) {

        log.info("收到用户{}的消息:{}", session.getId(), message);
    }
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误:{}", session.getId());
        error.printStackTrace();
    }

}
