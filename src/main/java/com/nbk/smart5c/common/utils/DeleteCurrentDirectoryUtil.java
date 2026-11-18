package com.nbk.smart5c.common.utils;

import lombok.var;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Comparator;

public class DeleteCurrentDirectoryUtil {

    /**
     * 方法1: 使用Files.walk删除当前目录及其所有内容
     */
    public static boolean deleteCurrentDirectory(Path currentDir) {
        try {
            // 验证当前目录是否存在
            if (!Files.exists(currentDir) || !Files.isDirectory(currentDir)) {
                System.out.println("当前目录不存在或不是目录");
                return false;
            }

            // 使用Files.walk遍历并删除
            try (var paths = Files.walk(currentDir)) {
                // 按逆序排序（先删除文件，再删除目录）
                paths
                        .sorted(Comparator.reverseOrder())
                        .forEach(path -> {
                            try {
                                Files.delete(path);
                                System.out.println("已删除: " + path);
                            } catch (IOException e) {
                                System.err.println("无法删除: " + path + " - " + e.getMessage());
                            }
                        });
            }

            System.out.println("当前目录及其所有内容已成功删除");
            return true;

        } catch (IOException e) {
            System.err.println("删除失败: " + e.getMessage());
            return false;
        }
    }

    /**
     * 方法2: 使用Files.walkFileTree删除（更可控）
     */
    public static boolean deleteCurrentDirectoryWithWalkFileTree(Path currentDir) throws IOException {

        if (!Files.exists(currentDir)) {
            return true; // 目录已不存在
        }

        Files.walkFileTree(currentDir, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                System.out.println("已删除文件: " + file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                if (exc == null) {
                    Files.delete(dir);
                    System.out.println("已删除目录: " + dir);
                    return FileVisitResult.CONTINUE;
                } else {
                    throw exc; // 目录遍历失败
                }
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) {
                System.err.println("无法访问文件: " + file + " - " + exc.getMessage());
                return FileVisitResult.CONTINUE; // 继续处理其他文件
            }
        });

        return true;
    }

    /**
     * 方法3: 安全删除当前目录（先移动，再删除）
     * 这可以防止程序在删除自己运行时所在的目录
     */
    public static boolean safeDeleteCurrentDirectory(Path currentDir) throws IOException {
        Path parentDir = currentDir.getParent();

        if (parentDir == null) {
            throw new IOException("当前目录没有父目录，无法安全删除");
        }

        // 创建一个临时目录名
        String tempDirName = currentDir.getFileName().toString() + "_deleting_" + System.currentTimeMillis();
        Path tempPath = parentDir.resolve(tempDirName);

        // 1. 重命名当前目录
        Files.move(currentDir, tempPath, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("已重命名目录: " + currentDir + " -> " + tempPath);

        // 2. 删除重命名后的目录
        return deleteDirectoryRecursive(tempPath);
    }

    /**
     * 递归删除目录
     */
    private static boolean deleteDirectoryRecursive(Path dir) throws IOException {
        if (!Files.exists(dir)) {
            return true;
        }

        try (var stream = Files.walk(dir)) {
            stream
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            System.err.println("无法删除: " + path + " - " + e.getMessage());
                        }
                    });
        }

        return true;
    }

    /**
     * 创建测试文件和目录
     */
    private static void createTestFiles() throws IOException {
        Path currentDir = Paths.get("").toAbsolutePath();

        // 创建子目录
        Path subDir1 = currentDir.resolve("subdir1");
        Path subDir2 = currentDir.resolve("subdir2/nested");
        Files.createDirectories(subDir1);
        Files.createDirectories(subDir2);

        // 创建文件
        Files.write(subDir1.resolve("file1.txt"), "测试内容1".getBytes());
        Files.write(subDir2.resolve("file2.txt"), "测试内容2".getBytes());
        Files.write(currentDir.resolve("rootfile.txt"), "根目录文件".getBytes());

        // 创建只读文件
        Path readOnlyFile = currentDir.resolve("readonly.txt");
        Files.write(readOnlyFile, "只读文件".getBytes());
        readOnlyFile.toFile().setReadOnly();

        System.out.println("测试文件创建完成");
    }
}