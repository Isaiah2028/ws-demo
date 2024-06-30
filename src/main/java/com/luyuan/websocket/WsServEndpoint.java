package com.luyuan.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
@ServerEndpoint("/myWs")
public class WsServEndpoint {
    static Map<String,Session> sessionMap = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session  session){
        sessionMap.put(session.getId(),session);
        log.info("websocket is open.. ");
    }

    @OnMessage
    public String onMessage(String text){
        log.info("收到一条消息..", text);
        return "i got your message.";
    }

    @OnClose
    public void OnColose(Session session){
        sessionMap.remove(session.getId());
        log.info("websocket is close...");
    }

    @Scheduled(fixedRate = 2000)
    public void sendMsg() throws IOException {
        for (String key: sessionMap.keySet()) {
            sessionMap.get(key).getBasicRemote().sendText("来自服务端的心跳包");
        }
    }


}
