package com.example.xllamaserver.component;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.*;
import java.net.Socket;

@Component
@ServerEndpoint("/chat")
public class WebSocketServer {
    private Session session;

    private static CopyOnWriteArraySet<WebSocketServer> webSockets =new CopyOnWriteArraySet<>();
    private static Map<String,Session> sessionPool = new HashMap<String,Session>();

    private class LLMClient {
        private static String HOSTNAME = "127.0.0.1";
        private static int PORT = 8899;

        private static JSONObject socket(JSONObject send){
            String host = HOSTNAME;
            int port = PORT;

            try {
                Socket socket = new Socket(host, port);
                System.out.println("Connected to Python server");
                System.out.println("Send: " + send.toString());

                OutputStream outputStream = socket.getOutputStream();
                outputStream.write((send.toString() + "\r\n").getBytes());

                InputStream inputStream = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder jsonString = new StringBuilder();
                String recv = null;
                while((recv = reader.readLine())!=null) {
                    System.out.println(recv);
                    jsonString.append(recv);
                }
                System.out.println("Recv: " + jsonString);
                socket.close();
                return JSON.parseObject(jsonString.toString());
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

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
        LLMClient client = new LLMClient();
        HashMap<String, Object> chat = new HashMap<>();
        chat.put("query", message);
        chat.put("history", null);
        JSONObject send = new JSONObject();
        send.put("type", "chat");
        send.put("value", chat);
        JSONObject response = LLMClient.socket(send);
        assert response != null;
        String type = response.get("type").toString();
        JSONObject value = response.getJSONObject("value");
        // 通过 session ID 给当前用户发送消息
        sendOneMessage(session.getId(), value.get("response").toString());
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