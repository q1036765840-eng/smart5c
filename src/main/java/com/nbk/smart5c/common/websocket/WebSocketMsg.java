package com.nbk.smart5c.common.websocket;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class WebSocketMsg<T> {

    /** Gson 线程安全，复用单例，避免每次推送都新建 */
    private static final Gson GSON = new Gson();

    private String cmd;
    private String userId;
    private T msg;


    public static <T> String msg(String cmd, String userId, T data) {
        return GSON.toJson(new WebSocketMsg<>(cmd, userId, data));
    }
}