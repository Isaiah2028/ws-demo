package com.luyuan.config;

import com.luyuan.interceptor.MyWsHandler;
import com.luyuan.interceptor.MyWsInterceptor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import javax.annotation.Resource;

@Component
@EnableWebSocket
public class MyWsConfig implements WebSocketConfigurer {
    @Resource
    MyWsHandler myWsHandler;
    @Resource
    MyWsInterceptor myWsInterceptor;


    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(myWsHandler,"/myWs1")
                .addInterceptors(myWsInterceptor)
                .setAllowedOrigins("*");
    }
}
