package com.nbk.smart5c.common.utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RtspScreenshot {
    public static String getRtspScreenshotPath(String rtspUrl, String filePath) {
        String command = String.format("ffmpeg -i %s -vframes 1 %s", rtspUrl, filePath);
        try {
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor(); // 等待命令执行完成
            log.info("截图成功:{}", filePath);
            return filePath;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void main(String[] args) {
        System.out.println(getRtspScreenshotPath("rtsp://admin:nbk123456@192.168.5.130:554", "/home/qwe/jxb0622.jpg"));
    }
}