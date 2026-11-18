package com.nbk.smart5c.common.utils;

import lombok.var;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicLong;

public class DirectorySizeNIO {
    public static long getDirectorySize(Path dir) throws IOException {
        if (!Files.isDirectory(dir)) {
            throw new IllegalArgumentException("Path must be a directory");
        }
        try (var stream = Files.walk(dir)) {
            return stream
                    // 只统计文件，排除目录
                    .filter(Files::isRegularFile)
                    .mapToLong(p -> {
                        try {
                            return Files.size(p);
                        } catch (IOException e) {
                            System.err.println("无法读取文件大小: " + p);
                            return 0L;
                        }
                    })
                    .sum();
        }
    }

    // 方法1.2: 使用Files.walkFileTree (可处理符号链接)
    public static long getDirectorySizeRecursive(Path path) throws IOException {
        AtomicLong totalSize = new AtomicLong(0);

        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                if (Files.isRegularFile(file) && !Files.isSymbolicLink(file)) {
                    totalSize.addAndGet(attrs.size());
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) {
                // 记录访问失败的文件
                System.err.println("无法访问文件: " + file + ", 原因: " + exc.getMessage());
                return FileVisitResult.CONTINUE;
            }
        });

        return totalSize.get();
    }

    public static void main(String[] args) {
        Path directory = Paths.get("/path/to/directory");
        try {
            long totalBytes = getDirectorySize(directory);
            System.out.println("目录大小: " + totalBytes + " 字节");
        } catch (IOException e) {
            System.err.println("计算目录大小时出错: " + e.getMessage());
        }
    }
}