package com.nbk.smart5c.common.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;


/**
 * 面向浏览器的 websocket 服务端，负责登记连接并向前端推送任务/导出进度。
 */
@Component
@Slf4j
public class WebSocketServerHandler extends AbstractWebSocketHandler {

    /** 单次发送超过 5s 视为客户端过慢，断开该连接 */
    private static final int SEND_TIME_LIMIT = 5000;
    /** 待发送消息缓冲上限 1MB，超出即断开，避免慢客户端无限堆积内存 */
    private static final int BUFFER_SIZE_LIMIT = 1024 * 1024;


    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        // 装饰后同一连接的并发发送被串行化并受时间/缓冲上限约束，不会拖垮广播
        WebSocketSession safeSession =
                new ConcurrentWebSocketSessionDecorator(session, SEND_TIME_LIMIT, BUFFER_SIZE_LIMIT);
        WebSocketSessionManager.add(session.getId(), safeSession);
        log.debug("websocket 连接建立, sessionId={}, 当前连接数={}", session.getId(), WebSocketSessionManager.size());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        // 前端目前只接收推送，不需要上行处理；保留 @Override 以免后续误删导致框架静默丢弃
        log.debug("收到前端 websocket 消息, sessionId={}, payload={}", session.getId(), message.getPayload());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.warn("websocket 传输异常, sessionId={}: {}", session.getId(), exception.getMessage());
        WebSocketSessionManager.removeAndClose(session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.debug("websocket 连接关闭, sessionId={}, status={}", session.getId(), status.getCode());
        WebSocketSessionManager.removeAndClose(session.getId());
    }
}
