package com.nbk.smart5c.common.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketServerConfig implements WebSocketConfigurer {

    //向前端传值的
    @Autowired
    private WebSocketServerHandler serverSocketHandler;


    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // 允许跨域，方便本地调试，生产建议去掉
        registry
                .addHandler(serverSocketHandler, "/webSocket/msg")
                .setAllowedOrigins("*");
    }
}
