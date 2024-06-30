package com.luyuan.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.socket.WebSocketSession;

@Data
@AllArgsConstructor
public class SessionBean {
    private WebSocketSession webSocketSession;
    private Integer clientId;
}
