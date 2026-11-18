package com.nbk.smart5c.common.utils;

import cn.hutool.core.util.ZipUtil;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

@Slf4j
public class ZipProcessUtil {

    /**
     * 创建包含多个文件/文件夹的ZIP压缩包
     *
     * @param sourceFiles 源文件/文件夹列表
     * @param zipFilePath 目标ZIP文件路径
     * @return 是否创建成功
     */
    public static boolean createZip(List<File> sourceFiles, String zipFilePath) {
        try {
            // 使用Hutool的ZipUtil创建压缩包
            File zipFile = ZipUtil.zip(new File(zipFilePath), true, sourceFiles.toArray(new File[0]));
            return zipFile.exists() && zipFile.length() > 0;
        } catch (Exception e) {
            log.error("创建ZIP压缩包失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 创建包含多个文件/文件夹的ZIP压缩包，并输出到Servlet输出流
     *
     * @param sourceFiles 源文件/文件夹列表
     * @param response    HTTP响应对象
     * @param zipFileName ZIP文件名
     */
    public static void createZipToResponse(List<File> sourceFiles, HttpServletResponse response, String zipFileName) {
        // 创建临时ZIP文件
        File tempZipFile = null;
        try {
            // 设置响应头
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/zip");
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + java.net.URLEncoder.encode(zipFileName, "UTF-8"));

            // 创建临时ZIP文件
            String tempDir = System.getProperty("java.io.tmpdir");
            tempZipFile = new File(tempDir, "temp_" + System.currentTimeMillis() + ".zip");

            // 创建压缩包
            if (!createZip(sourceFiles, tempZipFile.getAbsolutePath())) {
                throw new RuntimeException("创建ZIP文件失败");
            }

            // 将ZIP文件写入响应输出流
            try (InputStream in = new FileInputStream(tempZipFile);
                 OutputStream out = response.getOutputStream()) {

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
                out.flush();
            }

        } catch (Exception e) {
            log.error("输出ZIP压缩包失败: {}", e.getMessage());
            throw new RuntimeException("导出ZIP压缩包失败", e);
        } finally {
            // 清理临时文件
            if (tempZipFile != null && tempZipFile.exists()) {
                if (!tempZipFile.delete()) {
                    log.warn("临时ZIP文件删除失败: {}", tempZipFile.getAbsolutePath());
                }
            }
        }
    }
}