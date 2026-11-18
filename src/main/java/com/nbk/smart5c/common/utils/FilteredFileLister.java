package com.nbk.smart5c.common.utils;

import cn.hutool.core.text.CharSequenceUtil;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Log4j2
public class FilteredFileLister {

    /**
     * 按文件扩展名过滤
     */
    public static List<String> listFilesByExtension(String folderPath) {
        List<String> fileNames = new ArrayList<>();
        File folder = new File(folderPath);

        if (!folder.exists() || !folder.isDirectory()) {
            return fileNames;
        }

        File[] files = folder.listFiles();
        if (files == null) {
            return fileNames;
        }

        for (File file : files) {
            if (file.isFile() && FileUtil.judgeImageFile(file)) {
                fileNames.add(file.getName());
            }
        }
        return fileNames;
    }

    /**
     * 按文件扩展名过滤出图片文件完整路径
     */
    public static List<String> listFilepathByExtension(String folderPath) {
        List<String> filePaths = new ArrayList<>();
        File folder = new File(folderPath);

        if (!folder.exists() || !folder.isDirectory()) {
            return filePaths;
        }

        File[] files = folder.listFiles();
        if (files == null) {
            return filePaths;
        }

        for (File file : files) {
            if (file.isFile() && FileUtil.judgeImageFile(file)) {
                filePaths.add(file.getAbsolutePath());
            }
        }
        return filePaths;
    }

    /**
     * 按文件扩展名过滤出图片文件完整路径
     */
    public static void listFilepathByExtension(List<String> filePaths, String folderPath) {
        File folder = new File(folderPath);

        if (!folder.exists() || !folder.isDirectory()) {
            return;
        }

        File[] files = folder.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isFile() && FileUtil.judgeImageFile(file)) {
                filePaths.add(file.getAbsolutePath());
            } else if (file.isDirectory()) {
                listFilepathByExtension(filePaths, file.getAbsolutePath());
            }
        }
    }

    /**
     * 按文件扩展名过滤出图片文件完整路径
     */
    public static List<String> listDirInFolder(String folderPath) {
        List<String> filePaths = new ArrayList<>();
        File folder = new File(folderPath);

        if (!folder.exists() || !folder.isDirectory()) {
            return filePaths;
        }

        File[] files = folder.listFiles();
        if (files == null) {
            return filePaths;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                filePaths.add(file.getAbsolutePath());
            }
        }
        return filePaths;
    }

    /**
     * 按文件扩展名过滤出单个文件名
     */
    public static String getFirstFileByExtension(String folderPath) {
        File folder = new File(folderPath);

        if (CharSequenceUtil.isBlank(folderPath) || !folder.exists() || !folder.isDirectory()) {
            return "";
        }

        File[] files = folder.listFiles();
        if (files == null) {
            return "";
        }

        for (File file : files) {
            if (file.isFile() && FileUtil.judgeImageFile(file)) {
                return file.getName();
            } else if (file.isDirectory()) {
                log.info("getFirstFileByExtension_isdir:{}", file.getAbsolutePath());
                File[] childFiles = file.listFiles();
                if (childFiles == null) {
                    return "";
                }
                for (File file1 : childFiles) {
                    if (file1.isFile() && FileUtil.judgeImageFile(file1)) {
                        String fileName = file1.getAbsolutePath().replace(folderPath, "");
                        log.info("getFirstFileByExtension_file:{}", fileName);
                        if (fileName.startsWith("/")) {
                            return fileName.substring(1);
                        }
                    }
                }
            }
        }
        return "";
    }

    /**
     * 按多个扩展名过滤
     */
    public static List<String> listFilesByExtensions(String folderPath, String... extensions) {
        List<String> fileNames = new ArrayList<>();
        File folder = new File(folderPath);

        if (!folder.exists() || !folder.isDirectory()) {
            return fileNames;
        }

        File[] files = folder.listFiles();
        if (files == null) {
            return fileNames;
        }

        Set<String> extSet = new HashSet<>(Arrays.asList(extensions));

        for (File file : files) {
            if (file.isFile()) {
                String fileName = file.getName().toLowerCase();
                for (String ext : extSet) {
                    if (fileName.endsWith("." + ext.toLowerCase())) {
                        fileNames.add(file.getName());
                        break;
                    }
                }
            }
        }

        return fileNames;
    }

    /**
     * 按文件名模式过滤（正则表达式）
     */
    public static List<String> listFilesByPattern(String folderPath, String regexPattern) {
        List<String> fileNames = new ArrayList<>();
        File folder = new File(folderPath);
        Pattern pattern = Pattern.compile(regexPattern);

        if (!folder.exists() || !folder.isDirectory()) {
            return fileNames;
        }

        File[] files = folder.listFiles();
        if (files == null) {
            return fileNames;
        }

        for (File file : files) {
            if (file.isFile() && pattern.matcher(file.getName()).matches()) {
                fileNames.add(file.getName());
            }
        }

        return fileNames;
    }

    /**
     * 按文件大小过滤
     */
    public static List<String> listFilesBySize(String folderPath, long minSize, long maxSize) {
        List<String> fileNames = new ArrayList<>();
        File folder = new File(folderPath);

        if (!folder.exists() || !folder.isDirectory()) {
            return fileNames;
        }

        File[] files = folder.listFiles();
        if (files == null) {
            return fileNames;
        }

        for (File file : files) {
            if (file.isFile()) {
                long fileSize = file.length();
                if (fileSize >= minSize && fileSize <= maxSize) {
                    fileNames.add(file.getName());
                }
            }
        }

        return fileNames;
    }

    /**
     * 按最后修改时间过滤
     */
    public static List<String> listFilesByDate(String folderPath, Date afterDate, Date beforeDate) {
        List<String> fileNames = new ArrayList<>();
        File folder = new File(folderPath);

        if (!folder.exists() || !folder.isDirectory()) {
            return fileNames;
        }

        File[] files = folder.listFiles();
        if (files == null) {
            return fileNames;
        }

        long afterTime = afterDate != null ? afterDate.getTime() : 0;
        long beforeTime = beforeDate != null ? beforeDate.getTime() : Long.MAX_VALUE;

        for (File file : files) {
            if (file.isFile()) {
                long lastModified = file.lastModified();
                if (lastModified >= afterTime && lastModified <= beforeTime) {
                    fileNames.add(file.getName());
                }
            }
        }

        return fileNames;
    }

    /**
     * 按文件名前缀过滤
     */
    public static List<String> listFilesByPrefix(String folderPath, String prefix) {
        List<String> fileNames = new ArrayList<>();
        File folder = new File(folderPath);

        if (!folder.exists() || !folder.isDirectory()) {
            return fileNames;
        }

        File[] files = folder.listFiles();
        if (files == null) {
            return fileNames;
        }

        for (File file : files) {
            if (file.isFile() && file.getName().startsWith(prefix)) {
                fileNames.add(file.getName());
            }
        }

        return fileNames;
    }

    /**
     * 按文件名包含的字符串过滤
     */
    public static List<String> listFilesContaining(String folderPath, String containsText) {
        List<String> fileNames = new ArrayList<>();
        File folder = new File(folderPath);

        if (!folder.exists() || !folder.isDirectory()) {
            return fileNames;
        }

        File[] files = folder.listFiles();
        if (files == null) {
            return fileNames;
        }

        for (File file : files) {
            if (file.isFile() && file.getName().toLowerCase().contains(containsText.toLowerCase())) {
                fileNames.add(file.getName());
            }
        }

        return fileNames;
    }


    /**
     * 递归统计指定目录下所有XML文件数量
     */
    public static long countXmlFiles(String directoryPath) throws IOException {
        Path path = Paths.get(directoryPath);

        // 使用 Files.walk 递归遍历所有文件
        try (Stream<Path> paths = Files.walk(path)) {
            return paths
                    .filter(Files::isRegularFile)  // 只统计普通文件
                    .filter(p -> p.toString().toLowerCase().endsWith(".xml"))  // 筛选XML文件
                    .count();  // 计数
        }
    }
}