package com.example.xllamaserver.component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;


import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Component
@ServerEndpoint("/chat")
public class WebSocketServer {

    private Session session;

    private static CopyOnWriteArraySet<WebSocketServer> webSockets =new CopyOnWriteArraySet<>();
    private static Map<String,Session> sessionPool = new HashMap<String,Session>();

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        webSockets.add(this);
        sessionPool.put(session.getId(), session); // 使用 session ID 作为键
        System.out.println("【websocket消息】有新的连接，总数为: " + webSockets.size() + "，session ID: " + session.getId());
    }

    @OnClose
    public void onClose() {
        webSockets.remove(this);
        sessionPool.remove(this.session.getId()); // 断开连接时，移除对应的 session
        System.out.println("【websocket消息】连接断开，总数为: " + webSockets.size() + "，session ID: " + this.session.getId());
    }


    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("【websocket消】收到客户端消息: " + message + "，来自 session ID: " + session.getId());
        //TODO: 根据用户发送的message生成回复

        // 通过 session ID 给当前用户发送消息
        sendOneMessage(session.getId(), message);
    }

    // 此为广播消息
    public void sendAllMessage(String message) {
        for(WebSocketServer webSocket : webSockets) {
            System.out.println("【websocket消息】广播消息:"+message);
            try {
                webSocket.session.getAsyncRemote().sendText(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void sendOneMessage(String sessionId, String message) {
        System.out.println("【websocket消息】单点消息，发送到 session ID: " + sessionId);
        Session session = sessionPool.get(sessionId);
        if (session != null) {
            try {
                session.getAsyncRemote().sendText(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



}