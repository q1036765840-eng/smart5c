package com.nbk.smart5c.common.utils;

import com.nbk.smart5c.pojo.vo.CheckResult;

import java.net.InetSocketAddress;
import java.net.Socket;

public class PortChecker {

    public static CheckResult checkPort(String ip, int port, long timeoutMs) {
        // 用 TCP connect 替代 fork "nc -zv"：避免进程创建开销，且 connect 自身即受 timeoutMs 约束
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(ip, port), (int) Math.min(timeoutMs, Integer.MAX_VALUE));
            return new CheckResult(ip, port, true, null);
        } catch (Exception e) {
            return new CheckResult(ip, port, false, e.getMessage());
        }
    }
}
