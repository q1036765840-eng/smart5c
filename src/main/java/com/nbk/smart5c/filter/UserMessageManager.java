package com.nbk.smart5c.filter;

import cn.hutool.core.lang.Singleton;
import lombok.Data;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 *
 */
@Data
public class UserMessageManager {
    public static volatile UserMessageManager instance;
    private volatile ConcurrentMap<String, Integer> messageMap = new ConcurrentHashMap<>();
    private volatile ConcurrentMap<String, Integer> childMsgMap = new ConcurrentHashMap<>();

    private UserMessageManager() {
    }

    public static UserMessageManager getInstance() {
        //第一次检查，如果instance不为null,则直接返回实例，减少同步的开销
        if (instance == null) {
            //同步代码块，只有在instance为null时才进行同步
            synchronized (Singleton.class) {
                //第二次检查，进入同步代码块后再次检查instance是否为null,防止多个线程同时进入同步块
                if (instance == null) {
                    //只有一个线程能够执行到这里，创建实例
                    instance = new UserMessageManager();
                }
            }
        }
        return instance;
    }

    public void setMessageMap(String messageType, Integer unreadCount) {
        messageMap.put(messageType, unreadCount);
    }

    public void setChildMsgMap(String childType, Integer unreadCount) {
        childMsgMap.put(childType, unreadCount);
    }
}
