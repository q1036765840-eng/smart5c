package com.nbk.smart5c.common.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DirectoryLister {

    /**
     * 递归列举所有包含文件的目录
     */
    public static List<File> listDirectoriesWithFiles(String dirPath) {
        List<File> result = new ArrayList<>();
        File directory = new File(dirPath);

        if (!directory.exists() || !directory.isDirectory()) {
            System.err.println("目录不存在或不是有效目录: " + dirPath);
            return result;
        }

        listDirectoriesWithFilesRecursive(directory, result);
        return result;
    }

    /**
     * 递归核心方法
     */
    private static void listDirectoriesWithFilesRecursive(File dir, List<File> result) {
        if (dir == null || !dir.isDirectory()) {
            return;
        }

        File[] files = dir.listFiles();
        if (files == null) {
            return; // 访问权限问题，跳过该目录
        }

        boolean hasFile = false;

        // 检查当前目录是否包含文件
        for (File file : files) {
            if (file.isFile()) {
                hasFile = true;
                break;
            }
        }

        // 如果包含文件，添加到结果列表
        if (hasFile) {
            result.add(dir);
        }

        // 递归处理所有子目录
        for (File file : files) {
            if (file.isDirectory()) {
                listDirectoriesWithFilesRecursive(file, result);
            }
        }
    }

    /**
     * 递归列举所有包含文件的目录，并统计文件数量
     */
    public static Map<File, Integer> listDirectoriesWithFileCount(String dirPath) {
        Map<File, Integer> result = new HashMap<>();
        File directory = new File(dirPath);

        if (!directory.exists() || !directory.isDirectory()) {
            System.err.println("目录不存在或不是有效目录: " + dirPath);
            return result;
        }

        listDirectoriesWithFileCountRecursive(directory, result);
        return result;
    }

    /**
     * 递归统计每个目录的文件数量
     */
    private static void listDirectoriesWithFileCountRecursive(File dir, Map<File, Integer> result) {
        if (dir == null || !dir.isDirectory()) {
            return;
        }

        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }

        int fileCount = 0;
        List<File> subDirectories = new ArrayList<>();

        // 统计文件数量并收集子目录
        for (File file : files) {
            if (file.isFile()) {
                fileCount++;
            } else if (file.isDirectory()) {
                subDirectories.add(file);
            }
        }

        // 如果目录包含文件，添加到结果
        if (fileCount > 0) {
            result.put(dir, fileCount);
        }

        // 递归处理所有子目录
        for (File subDir : subDirectories) {
            listDirectoriesWithFileCountRecursive(subDir, result);
        }
    }
}