package com.nbk.smart5c.common.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class FileSearcherUtil {

    /**
     * 使用Files.walk递归搜索文件，按文件名包含关键字（字面匹配，忽略大小写）。
     * 注意：keyword 按普通字符串处理，内部会做正则转义，调用方传入用户输入无需担心正则元字符。
     *
     * @param directory 搜索的起始目录
     * @param keyword   文件名关键字（普通字符串，非正则）
     * @return 匹配的文件路径列表；目录为空或无匹配返回空列表
     */
    public static List<Path> searchFiles(Path directory, String keyword) throws IOException {
        List<Path> result = new ArrayList<>();
        if (null == directory || null == keyword || keyword.isEmpty()) {
            return result;
        }
        // 转义用户输入，避免 . + ( [ { * ? 等元字符被当作正则语法导致语义错位或 PatternSyntaxException
        Pattern regex = Pattern.compile(Pattern.quote(keyword), Pattern.CASE_INSENSITIVE);

        Files.walk(directory)
                .filter(Files::isRegularFile)
                .filter(path -> {
                    java.nio.file.Path fileName = path.getFileName();
                    return null != fileName && regex.matcher(fileName.toString()).find();
                })
                .forEach(result::add);

        return result;
    }

    /**
     * 递归搜索文件及目录，按名称包含关键字（字面匹配，忽略大小写）。
     * 与 {@link #searchFiles(Path, String)} 的区别：本方法会同时返回名称匹配的目录，
     * 起始目录本身不会被包含在结果中。
     *
     * @param directory 搜索的起始目录
     * @param keyword   文件名关键字（普通字符串，非正则）
     * @return 匹配的文件/目录路径列表；目录为空或无匹配返回空列表
     */
    public static List<Path> searchFilesAndDirs(Path directory, String keyword) throws IOException {
        List<Path> result = new ArrayList<>();
        if (null == directory || null == keyword || keyword.isEmpty()) {
            return result;
        }
        Pattern regex = Pattern.compile(Pattern.quote(keyword), Pattern.CASE_INSENSITIVE);

        Files.walk(directory)
                .filter(path -> !path.equals(directory))
                .filter(path -> {
                    Path fileName = path.getFileName();
                    return null != fileName && regex.matcher(fileName.toString()).find();
                })
                .forEach(result::add);

        return result;
    }

    /**
     * 支持通配符的搜索（如 *.txt, test*.java）
     */
    public static List<Path> searchFilesWithWildcard(Path directory, String wildcard) throws IOException {
        // 将通配符转换为正则表达式
        String regex = wildcardToRegex(wildcard);
        return searchFiles(directory, regex);
    }

    /**
     * 通配符转正则表达式
     */
    private static String wildcardToRegex(String wildcard) {
        StringBuilder regex = new StringBuilder();
        for (char c : wildcard.toCharArray()) {
            switch (c) {
                case '*':
                    regex.append(".*");
                    break;
                case '?':
                    regex.append(".");
                    break;
                case '.':
                case '[':
                case ']':
                case '(':
                case ')':
                case '\\':
                case '^':
                case '$':
                case '|':
                case '+':
                case '{':
                case '}':
                    regex.append("\\").append(c);
                    break;
                default:
                    regex.append(c);
            }
        }
        return regex.toString();
    }

    public static void main(String[] args) {
        try {
            Path currentDir = Paths.get(".");

            // 示例1：搜索所有.txt文件
            System.out.println("搜索所有.txt文件:");
            List<Path> txtFiles = searchFiles(currentDir, ".*\\.txt$");
            txtFiles.forEach(System.out::println);

            // 示例2：搜索包含"test"的文件
            System.out.println("\n搜索包含'test'的文件:");
            List<Path> testFiles = searchFiles(currentDir, ".*test.*");
            testFiles.forEach(System.out::println);

            // 示例3：使用通配符搜索
            System.out.println("\n使用通配符搜索*.java文件:");
            List<Path> javaFiles = searchFilesWithWildcard(currentDir, "*.java");
            javaFiles.forEach(System.out::println);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}