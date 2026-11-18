package com.nbk.smart5c.common.utils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class DirectoryCopyUtil {

    /**
     * 复制整个目录到目标位置（最简方法）
     *
     * @param sourceDir 源目录
     * @param targetDir 目标目录
     * @param overwrite 是否覆盖已存在的文件
     * @return 是否复制成功
     */
    public static boolean copyDirectory(String sourceDir, String targetDir, boolean overwrite) {
        Path sourcePath = Paths.get(sourceDir);
        Path targetPath = Paths.get(targetDir);

        try {
            // 确保源目录存在且是目录
            if (!Files.exists(sourcePath) || !Files.isDirectory(sourcePath)) {
                System.err.println("源目录不存在或不是目录: " + sourceDir);
                return false;
            }

            // 使用Files.walk遍历所有文件
            Files.walk(sourcePath)
                    .forEach(source -> {
                        try {
                            // 计算目标路径
                            Path destination = targetPath.resolve(sourcePath.relativize(source));

                            if (Files.isDirectory(source)) {
                                // 如果是目录，确保目标目录存在
                                if (!Files.exists(destination)) {
                                    Files.createDirectories(destination);
                                    System.out.println("创建目录: " + destination);
                                }
                            } else {
                                // 如果是文件，复制文件
                                if (overwrite || !Files.exists(destination)) {
                                    Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
                                    System.out.println("复制文件: " + source + " -> " + destination);
                                } else {
                                    System.out.println("文件已存在，跳过: " + destination);
                                }
                            }
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    });

            System.out.println("目录复制完成: " + sourceDir + " -> " + targetDir);
            return true;

        } catch (UncheckedIOException e) {
            System.err.println("复制过程中出错: " + e.getCause().getMessage());
            return false;
        } catch (IOException e) {
            System.err.println("复制目录失败: " + e.getMessage());
            return false;
        }
    }

    /**
     * 复制目录并显示进度
     */
    public static boolean copyDirectoryWithProgress(String sourceDir, String targetDir,
                                                    boolean overwrite, ProgressCallback callback) {
        Path sourcePath = Paths.get(sourceDir);
        Path targetPath = Paths.get(targetDir);

        try {
            if (!Files.exists(sourcePath) || !Files.isDirectory(sourcePath)) {
                return false;
            }

            // 统计文件总数
            long totalFiles = Files.walk(sourcePath)
                    .filter(Files::isRegularFile)
                    .count();

            if (callback != null) {
                callback.onStart(totalFiles);
            }

            final long[] copiedFiles = {0};

            // 遍历并复制
            Files.walk(sourcePath)
                    .forEach(source -> {
                        try {
                            Path destination = targetPath.resolve(sourcePath.relativize(source));

                            if (Files.isDirectory(source)) {
                                if (!Files.exists(destination)) {
                                    Files.createDirectories(destination);
                                }
                            } else {
                                if (overwrite || !Files.exists(destination)) {
                                    Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
                                    copiedFiles[0]++;

                                    if (callback != null) {
                                        callback.onProgress(copiedFiles[0], totalFiles,
                                                source.toString(), destination.toString());
                                    }
                                }
                            }
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    });

            if (callback != null) {
                callback.onComplete(copiedFiles[0], totalFiles);
            }

            return true;

        } catch (IOException e) {
            if (callback != null) {
                callback.onError(e.getMessage());
            }
            return false;
        }
    }

    /**
     * 复制目录并排除特定文件/目录
     */
    public static boolean copyDirectoryWithExclusion(String sourceDir, String targetDir,
                                                     boolean overwrite, String[] excludePatterns) {
        Path sourcePath = Paths.get(sourceDir);
        Path targetPath = Paths.get(targetDir);

        try {
            if (!Files.exists(sourcePath) || !Files.isDirectory(sourcePath)) {
                return false;
            }

            Files.walk(sourcePath)
                    .forEach(source -> {
                        try {
                            // 检查是否需要排除
                            if (shouldExclude(source, sourcePath, excludePatterns)) {
                                System.out.println("排除: " + source);
                                return;
                            }

                            Path destination = targetPath.resolve(sourcePath.relativize(source));

                            if (Files.isDirectory(source)) {
                                if (!Files.exists(destination)) {
                                    Files.createDirectories(destination);
                                }
                            } else {
                                if (overwrite || !Files.exists(destination)) {
                                    Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
                                }
                            }
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    });

            return true;

        } catch (IOException e) {
            System.err.println("复制目录失败: " + e.getMessage());
            return false;
        }
    }

    /**
     * 检查文件/目录是否应该被排除
     */
    private static boolean shouldExclude(Path path, Path basePath, String[] excludePatterns) {
        if (excludePatterns == null) return false;

        // 获取相对路径
        Path relativePath = basePath.relativize(path);
        String pathString = relativePath.toString().replace("\\", "/");

        for (String pattern : excludePatterns) {
            if (pathString.matches(convertGlobToRegex(pattern))) {
                return true;
            }
        }

        return false;
    }

    /**
     * 将glob模式转换为正则表达式
     */
    private static String convertGlobToRegex(String glob) {
        StringBuilder regex = new StringBuilder();

        for (int i = 0; i < glob.length(); i++) {
            char c = glob.charAt(i);
            switch (c) {
                case '*':
                    regex.append(".*");
                    break;
                case '?':
                    regex.append(".");
                    break;
                case '.':
                    regex.append("\\.");
                    break;
                default:
                    regex.append(c);
            }
        }

        return regex.toString();
    }

    /**
     * 进度回调接口
     */
    public interface ProgressCallback {
        void onStart(long totalFiles);

        void onProgress(long current, long total, String sourceFile, String targetFile);

        void onError(String errorMessage);

        void onComplete(long copiedFiles, long totalFiles);
    }
}