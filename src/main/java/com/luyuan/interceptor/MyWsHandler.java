package com.luyuan.interceptor;

import com.luyuan.beans.SessionBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 主逻辑处理类
 */
@Component
@Slf4j
public class MyWsHandler extends AbstractWebSocketHandler {
    private static Map<String, SessionBean> sessionBeanMap;
    private static AtomicInteger clientIdMaker;
    private static StringBuffer stringBuffer;


    static {
        sessionBeanMap = new ConcurrentHashMap<>();
        clientIdMaker = new AtomicInteger(0);
       stringBuffer = new StringBuffer();
    }


    //建立连接
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        SessionBean sessionBean = new SessionBean(session,clientIdMaker.getAndIncrement());
        sessionBeanMap.put(session.getId(),sessionBean);
        log.info(sessionBeanMap.get(session.getId()).getClientId() + "建立了连接...");
        super.afterConnectionEstablished(session);
        stringBuffer.append(sessionBeanMap.get(session.getId()).getClientId() + " 进入了群聊...<br/>");
        //广播群聊消息
        sendMessage(sessionBeanMap);
    }

    //收到消息
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.info(sessionBeanMap.get(session.getId()).getClientId() + ": " +message.getPayload());
        stringBuffer.append(sessionBeanMap.get(session.getId()).getClientId() + ": " +message.getPayload() + "<br/ >");
        super.handleTextMessage(session, message);
    }

    //传输异常
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        super.handleTransportError(session, exception);
        if (session.isOpen()){
            session.close();
        }
            sessionBeanMap.remove(session.getId());
        }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        Integer clientId = sessionBeanMap.get(session.getId()).getClientId();

        sessionBeanMap.remove(session.getId());
        log.info(clientId + "关闭了连接...");
        sendMessage(sessionBeanMap);
        stringBuffer.append( clientId + ": 退出了群聊 <br/ >");
        sendMessage(sessionBeanMap);


    }


    private void sendMessage(Map<String, SessionBean> sessionBeanMap) {
        for (String key : sessionBeanMap.keySet()) {
            try {
                sessionBeanMap.get(key).getWebSocketSession().sendMessage(new TextMessage(stringBuffer.toString()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


//    @Scheduled(fixedRate = 2000)
//    public void sendMsg() throws IOException {
//        for (String key: sessionBeanMap.keySet()) {
//            sessionBeanMap.get(key).getWebSocketSession().sendMessage(new TextMessage("来自服务端的心跳包"));
//        }
//    }

    
    

}
