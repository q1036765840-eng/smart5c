package com.nbk.smart5c.common.utils;

import java.io.File;

public class FileParentFinder {

    /**
     * 向上递归查找包含文件的父目录
     * 从当前目录开始，向上查找直到找到包含文件的目录
     */
    public static File findParentWithFiles(File startDir) {
        if (startDir == null || !startDir.exists()) {
            return null;
        }

        File currentDir = startDir;

        while (currentDir != null) {
            // 检查当前目录是否包含文件
            if (hasFiles(currentDir)) {
                return currentDir;
            }

            // 如果当前目录不包含文件，向上一级
            currentDir = currentDir.getParentFile();
        }

        return null; // 没有找到包含文件的目录
    }

    /**
     * 检查目录是否包含文件（不包括子目录）
     */
    private static boolean hasFiles(File directory) {
        if (directory == null || !directory.isDirectory()) {
            return false;
        }

        File[] files = directory.listFiles();
        if (files == null) {
            return false;
        }

        for (File file : files) {
            if (file.isFile()) {
                return true;
            }
        }

        return false;
    }

    /**
     * 检查目录是否包含文件（包括子目录中的文件）
     */
    private static boolean hasFilesRecursive(File directory) {
        if (directory == null || !directory.isDirectory()) {
            return false;
        }

        File[] files = directory.listFiles();
        if (files == null) {
            return false;
        }

        for (File file : files) {
            if (file.isFile()) {
                return true;
            } else if (file.isDirectory()) {
                // 递归检查子目录
                if (hasFilesRecursive(file)) {
                    return true;
                }
            }
        }

        return false;
    }
}