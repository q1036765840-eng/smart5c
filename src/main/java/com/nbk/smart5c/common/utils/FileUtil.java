package com.nbk.smart5c.common.utils;

import cn.hutool.core.util.StrUtil;
import org.apache.commons.lang3.StringUtils;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author fhey
 * @date 2023-04-20 11:15:58
 * @description: 文件工具类
 */
public class FileUtil {
    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

    //获取新文件的全路径
    public static String getNewFileFullPath(String sourceFilePath, String destFilePath, String ext) {
        File destFile = new File(destFilePath);
        if (destFile.isFile()) {
            return destFilePath;
        }
        File sourceFile = new File(sourceFilePath);
        String sourceFileName = sourceFile.getName();
        if (sourceFile.isFile()) {
            return destFilePath + File.separator + sourceFileName.substring(0, sourceFileName.lastIndexOf(StrUtil.DOT)) + StrUtil.DOT + ext;
        }
        return destFilePath + File.separator + sourceFileName + StrUtil.DOT + ext;
    }

    //判断文件是否是图片
    public static boolean isImage(File file) throws IOException {
        FileInputStream is = new FileInputStream(file);
        byte[] bytes = new byte[8];
        is.read(bytes);
        is.close();
        String type = bytesToHexString(bytes).toUpperCase();
        if (type.contains("FFD8FF") //JPEG(jpg)
                || type.contains("89504E47") //PNG
                || type.contains("47494638") //GIF
                || type.contains("49492A00") //TIFF(tif)
                || type.contains("424D") //Bitmap(bmp)
        ) {
            return true;
        }
        return false;
    }

    //将文件头转换成16进制字符串
    public static String bytesToHexString(byte[] src) {
        StringBuilder builder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                builder.append(0);
            }
            builder.append(hv);
        }
        return builder.toString();
    }

    public static boolean judgeImageFile(String fileName) {
        return fileName.endsWith(".jpg")
                || fileName.endsWith(".jpeg")
                || fileName.endsWith(".png")
                || fileName.endsWith(".gif")
                || fileName.endsWith(".bmp")
                || fileName.endsWith(".amv")
                || fileName.endsWith(".adx")
                || fileName.endsWith(".ix2");
    }

    public static boolean judgeImageFile(File file) {
        String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".jpg")
                || fileName.endsWith(".jpeg")
                || fileName.endsWith(".png")
                || fileName.endsWith(".gif")
                || fileName.endsWith(".bmp")
                || fileName.endsWith(".amv")
                || fileName.endsWith(".adx")
                || fileName.endsWith(".ix2");
    }

    /**
     * 递归删除目录下文件名（不含扩展名）以 _md、_sl 结尾的图片，如 xxx_md.jpg、xxx_sl.png
     *
     * @return 删除的文件数量
     */
    public static int deleteMdSlImages(File dir) {
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            return 0;
        }
        int deleted = 0;
        for (File file : files) {
            if (file.isDirectory()) {
                deleted += deleteMdSlImages(file);
                continue;
            }
            if (!judgeImageFile(file)) {
                continue;
            }
            String name = file.getName();
            int dotIndex = name.lastIndexOf('.');
            String baseName = (dotIndex > 0 ? name.substring(0, dotIndex) : name).toLowerCase();
            if (baseName.endsWith("_md") || baseName.endsWith("_sl")) {
                if (file.delete()) {
                    deleted++;
                } else {
                    logger.warn("删除 _md/_sl 图片失败: {}", file.getAbsolutePath());
                }
            }
        }
        return deleted;
    }

    public static boolean judgeImageFileOrXml(File file) {
        String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".jpg")
                || fileName.endsWith(".jpeg")
                || fileName.endsWith(".png")
                || fileName.endsWith(".gif")
                || fileName.endsWith(".bmp")
                || fileName.endsWith(".amv")
                || fileName.endsWith(".adx")
                || fileName.endsWith(".ix2")
                || fileName.endsWith(".xml")
                ;
    }

    public static boolean judgeVideoFile(File file) {
        String fileName = file.getName();
        int lastDotIndex = fileName.lastIndexOf('.');
        String extension = (lastDotIndex == -1) ? "" : fileName.substring(lastDotIndex + 1);
        return extension.equalsIgnoreCase("mp4")
                || extension.equalsIgnoreCase("avi")
                || extension.equalsIgnoreCase("mxf")
                || extension.equalsIgnoreCase("wav")
                || extension.equalsIgnoreCase("mov")
                || extension.equalsIgnoreCase("mpeg")
                || extension.equalsIgnoreCase("wmv")
                || extension.equalsIgnoreCase("flash")
                || extension.equalsIgnoreCase("mpeg-2")
                || extension.equalsIgnoreCase("mpeg-4")
                || extension.equalsIgnoreCase("MTS")
                || extension.equalsIgnoreCase("mkv");
        // 添加更多视频格式的判断条件，根据需要进行修改
    }

    /**
     * Copies folder.
     *
     * @param source source path must not be null
     * @param target target path must not be null
     */
    public static void copyFolder(@NonNull Path source, @NonNull Path target) throws IOException {
        Assert.notNull(source, "Source path must not be null");
        Assert.notNull(target, "Target path must not be null");

        Files.walkFileTree(source, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                    throws IOException {
                Path current = target.resolve(source.relativize(dir).toString());
                Files.createDirectories(current);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                    throws IOException {
                Files.copy(file, target.resolve(source.relativize(file).toString()),
                        StandardCopyOption.REPLACE_EXISTING);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * 将源目录复制到目标目录下（目标目录下会生成与源目录同名的文件夹）
     *
     * @param source 源目录路径
     * @param target 目标父目录路径
     * @throws IOException 如果复制过程中发生 I/O 错误
     */
    public static void copyDirectory(Path source, Path target) throws IOException {
        // 检查源目录是否存在且是一个目录
        if (!Files.exists(source) || !Files.isDirectory(source)) {
            throw new IllegalArgumentException("Source must be an existing directory: " + source);
        }

        // 在目标目录下创建与源目录同名的文件夹，例如 source=/a/b, target=/c  => 目标为 /c/b
        Path targetDir = target.resolve(source.getFileName());
        Files.createDirectories(targetDir);

        // 遍历源目录中的所有文件/子目录
        try (Stream<Path> stream = Files.walk(source)) {
            stream.forEach(path -> {
                try {
                    // 计算相对路径
                    Path relative = source.relativize(path);
                    Path destPath = targetDir.resolve(relative);
                    if (Files.isDirectory(path)) {
                        // 如果是目录，则创建对应的目录
                        Files.createDirectories(destPath);
                    } else {
                        // 如果是文件，则复制（如果目标已存在则覆盖）
                        Files.copy(path, destPath, StandardCopyOption.REPLACE_EXISTING);
                    }
                } catch (IOException e) {
                    throw new RuntimeException("Failed to copy: " + path, e);
                }
            });
        }
    }

    /**
     * 删除文件夹
     *
     * @param file
     */
    public static void deleteAllFolder(File file) {
        try {
            File[] files = file.listFiles();
            if (files == null || files.length == 0) {
                boolean delete = file.delete();
                return;
            }
            for (File f : files) {
                if (f.isFile()) {
                    boolean delete = f.delete();
                    continue;
                }
                if (f.isDirectory()) {
                    deleteAllFolder(f);
                    boolean delete = f.delete();
                    continue;
                }
            }
            boolean delete = file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteFolder(String filePath) {
        if (StringUtils.isBlank(filePath)) {
            return;
        }
        File file = new File(filePath);
        if (!file.exists()) {
            return;
        }
        deleteAllFolder(file);

    }


    /**
     * 删除文件
     *
     * @param filePaths
     */
    public static void deleteFile(List<String> filePaths) {
        if (filePaths.isEmpty()) return;
        for (String filePath : filePaths) {
            // deleteFile(filePath);
            FileUtil.deleteFile(new File(filePath));
        }
    }

    /**
     * 删除文件
     *
     * @param filePath
     */
    public static void deleteFile(String filePath) {
        File file = new File(filePath);
        if (!file.isFile()) {
            return;
        }
        File parentFile = file.getParentFile();
        boolean delete = file.delete();
        CircularDeletion(parentFile);
    }

    public static Boolean rename(String path) {
        Path oldFilePath = Paths.get(path);
        //应该来说是没有#在文件上的
        Path newFilePath = Paths.get(path.replaceAll("#", ""));
        try {
            Files.move(oldFilePath, newFilePath, StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 辅助方法，用于递归地遍历目录并重命名文件
     *
     * @param dir 要遍历的目录
     * @throws IOException 如果发生I/O错误
     */
    public static void renameFilesInDirectory(Path dir) throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path entry : stream) {
                if (Files.isRegularFile(entry) && entry.getFileName().toString().contains("#")) {
                    // 构建新的文件路径，将'#'替换为空格
                    Path newPath = dir.resolve(entry.getFileName().toString().replaceAll("#", ""));
                    // 重命名文件，如果目标文件已存在则替换它
                    Files.move(entry, newPath, StandardCopyOption.REPLACE_EXISTING);
                } else if (Files.isDirectory(entry)) {
                    // 如果是目录，则递归遍历该目录
                    renameFilesInDirectory(entry);
                }
            }
        }
    }

    public static void renameAllFiles(String filePath) {
        Path dir = Paths.get(filePath);
        if (!Files.isDirectory(dir)) {
            throw new IllegalArgumentException("提供的路径不是目录：" + filePath);
        }

        try {
            renameFilesInDirectory(dir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * long类型的内存转换成字符串
     *
     * @param num
     * @return
     */
    public static String numToStringSpace(long num) {
        if (num <= 0) {
            return "";
        }
        if (num < 1024) {
            return num + "B";
        }
        long kNum = num / 1024;
        if (kNum < 1024) {
            return kNum + "KB";
        }
        long mNum = kNum / 1024;
        if (mNum < 1024) {
            return mNum + "MB";
        }
        long gNum = mNum / 1024;
        if (gNum < 1024) {
            return gNum + "GB";
        }
        long tNum = gNum / 1024;
        return tNum + "TB";
    }


    /**
     * 删除文件
     *
     * @param file
     */
    public static void deleteFile(File file) {
        if (!file.exists()) {
            return;
        }
        if (!file.isFile()) {
            return;
        }
        boolean delete = file.delete();
        File parentFile = file.getParentFile();
        File[] files = parentFile.listFiles();
        if (Objects.isNull(files) || files.length == 0) {
            boolean delete1 = parentFile.delete();
        }
        //CircularDeletion(parentFile);
    }

    /**
     * 循环删除文件的空父文件夹
     *
     * @param file
     */
    private static void CircularDeletion(File file) {
        if (!file.isDirectory()) {
            return;
        }
        File[] files = file.listFiles();
        if (Objects.nonNull(files) && files.length == 0) {
            boolean delete = file.delete();
        } else {
            for (File file1 : files) {
                if (file1.isFile()) {
                    file1.delete();
                } else {
                    CircularDeletion(file1);
                }
            }
        }
        boolean delete1 = file.delete();
    }


    public static void deleteFileIncludeParentFolder(String filePath) {
        File file = new File(filePath);
        if (!file.isFile()) {
            return;
        }
        File parentFile = file.getParentFile();
        boolean delete = file.delete();
        CircularDeletionIncludFolder(parentFile);
    }

    private static void CircularDeletionIncludFolder(File parentFile) {
        if (!parentFile.isDirectory()) {
            return;
        }
        File[] files = parentFile.listFiles();
        if (Objects.nonNull(files) && files.length == 0) {
            File parentFile1 = parentFile.getParentFile();
            boolean delete1 = parentFile1.delete();
            CircularDeletionIncludFolder(parentFile1);
        }
    }

    public static String imageToBase64(String path) {
        if (StringUtils.isBlank(path)) {
            return "";
        }
        File file = new File(path);
        if (!file.exists()) {
            return "";
        }
        try {
            return Base64.getEncoder().encodeToString(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 跳帧
     *
     * @param videoFilePath
     * @param frameNumberToCapture
     * @return
     */
    public static String getFrameByVideoPath(String videoFilePath, Long frameNumberToCapture) {
        logger.info("开始跳帧");
        Date date1 = new Date();
        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(videoFilePath)) {
            // 初始化帧抓取器
            grabber.start();
            // 跳转到目标帧（注意：帧索引通常从0开始）
            grabber.setFrameNumber(Math.toIntExact(frameNumberToCapture));
            // 抓取并处理帧
            Frame frame = grabber.grabImage();
            if (frame != null) {
                Java2DFrameConverter converter = new Java2DFrameConverter();
                BufferedImage bufferedImage = converter.convert(frame);
                ByteArrayOutputStream stream = new ByteArrayOutputStream(1024 * 4);
                ImageIO.write(bufferedImage, "jpg", stream);
                stream.flush();
                byte[] bytes = stream.toByteArray();
                stream.close();
                //将字节数组转换成Base64编码的字符串
                // 关闭帧抓取器
                grabber.stop();
                logger.info("跳帧成功，传输视频帧信息!");
                Date date2 = new Date();
                logger.info("用时:{}", date2.getTime() - date1.getTime());
                return Base64.getEncoder().encodeToString(bytes);
            } else {
                logger.info("未能成功抓取帧,抓取的帧数是：" + frameNumberToCapture);
                // 关闭帧抓取器
                grabber.stop();
                return "";
            }
        } catch (FrameGrabber.Exception e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static BufferedImage getBufferedByVideoPath(String videoFilePath, Long frameNumberToCapture) {
        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(videoFilePath)) {
            // 初始化帧抓取器
            grabber.start();
            // 跳转到目标帧（注意：帧索引通常从0开始）
            grabber.setFrameNumber(Math.toIntExact(frameNumberToCapture));
            // 抓取并处理帧
            Frame frame = grabber.grabImage();
            if (frame != null) {
                Java2DFrameConverter converter = new Java2DFrameConverter();
                BufferedImage bufferedImage = converter.convert(frame);
                logger.info("图片缓冲成功!");
                return bufferedImage;
            } else {
                logger.info("图片缓冲失败！该帧数是：" + frameNumberToCapture);
                // 关闭帧抓取器
                grabber.stop();
                return null;
            }
        } catch (FrameGrabber.Exception e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Deletes folder recursively.
     *
     * @param deletingPath deleting path must not be null
     */
    public static void deleteFolder(@NonNull Path deletingPath) {
        Assert.notNull(deletingPath, "Deleting path must not be null");
        if (Files.notExists(deletingPath)) {
            return;
        }
        logger.info("Deleting [{}]", deletingPath);
        delete(deletingPath.toFile());
        logger.info("Deleted [{}] successfully", deletingPath);
    }

    private static void delete(File file) {
        if (file.isDirectory()) {
            Arrays.asList(Objects.requireNonNull(file.listFiles())).forEach(FileUtil::delete);
        }
        file.delete();
    }


    /**
     * Renames file or folder.
     *
     * @param pathToRename file path to rename must not be null
     * @param newName      new name must not be null
     */
    public static void rename(@NonNull Path pathToRename, @NonNull String newName)
            throws IOException {
        Assert.notNull(pathToRename, "File path to rename must not be null");
        Assert.notNull(newName, "New name must not be null");

        Path newPath = pathToRename.resolveSibling(newName);
        logger.info("Rename [{}] to [{}]", pathToRename, newPath);
        Files.move(pathToRename, newPath);
        logger.info("Rename [{}] successfully", pathToRename);
    }


    /**
     * Unzips content to the target path.
     *
     * @param zis        zip input stream must not be null
     * @param targetPath target path must not be null and not empty
     * @throws IOException throws when failed to access file to be unzipped
     */
    public static void unzip(@NonNull ZipInputStream zis, @NonNull Path targetPath)
            throws Exception {
        // 1. unzip file to folder
        // 2. return the folder path
        Assert.notNull(zis, "Zip input stream must not be null");
        Assert.notNull(targetPath, "Target path must not be null");

        // Create path if absent
        createIfAbsent(targetPath);
        // Folder must be empty
        ensureEmpty(targetPath);
        ZipEntry zipEntry = zis.getNextEntry();
        while (zipEntry != null) {
            // Resolve the entry path
            Path entryPath = targetPath.resolve(zipEntry.getName());
            // Check directory
            checkDirectoryTraversal(targetPath, entryPath);
            if (zipEntry.isDirectory()) {
                // Create directories
                Files.createDirectories(entryPath);
            } else {
                // Copy file
                Files.copy(zis, entryPath);
            }
            zipEntry = zis.getNextEntry();
        }
    }

    /**
     * Unzips content to the target path.
     *
     * @param bytes      zip bytes array must not be null
     * @param targetPath target path must not be null and not empty
     * @throws IOException io exception
     */
    public static void unzip(@NonNull byte[] bytes, @NonNull Path targetPath) throws Exception {
        Assert.notNull(bytes, "Zip bytes must not be null");
        ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(bytes));
        unzip(zis, targetPath);
    }

    /**
     * Creates directories if absent.
     *
     * @param path path must not be null
     * @throws IOException io exception
     */
    public static void createIfAbsent(@NonNull Path path) throws IOException {
        Assert.notNull(path, "Path must not be null");

        if (Files.notExists(path)) {
            // Create directories
            Files.createDirectories(path);

            logger.debug("Created directory: [{}]", path);
        }
    }

    /**
     * The given path must be empty.
     *
     * @param path path must not be null
     * @throws IOException io exception
     */
    public static void ensureEmpty(@NonNull Path path) throws IOException {
        if (!isEmpty(path)) {
            throw new DirectoryNotEmptyException("Target directory: " + path + " was not empty");
        }
    }

    /**
     * Checks directory traversal vulnerability.
     *
     * @param parentPath  parent path must not be null.
     * @param pathToCheck path to check must not be null
     */
    public static void checkDirectoryTraversal(@NonNull String parentPath,
                                               @NonNull String pathToCheck) throws Exception {
        checkDirectoryTraversal(Paths.get(parentPath), Paths.get(pathToCheck));
    }

    /**
     * Checks directory traversal vulnerability.
     *
     * @param parentPath  parent path must not be null.
     * @param pathToCheck path to check must not be null
     */
    public static void checkDirectoryTraversal(@NonNull Path parentPath,
                                               @NonNull String pathToCheck) throws Exception {
        checkDirectoryTraversal(parentPath, Paths.get(pathToCheck));
    }

    /**
     * Checks directory traversal vulnerability.
     *
     * @param parentPath  parent path must not be null.
     * @param pathToCheck path to check must not be null
     */
    public static void checkDirectoryTraversal(@NonNull Path parentPath,
                                               @NonNull Path pathToCheck) throws Exception {
        Assert.notNull(parentPath, "Parent path must not be null");
        Assert.notNull(pathToCheck, "Path to check must not be null");

        if (pathToCheck.normalize().startsWith(parentPath)) {
            return;
        }

        throw new Exception("你没有权限访问 " + pathToCheck);
    }

    /**
     * Checks if the given path is empty.
     *
     * @param path path must not be null
     * @return true if the given path is empty; false otherwise
     * @throws IOException io exception
     */
    public static boolean isEmpty(@NonNull Path path) throws IOException {
        Assert.notNull(path, "Path must not be null");

        if (!Files.isDirectory(path) || Files.notExists(path)) {
            return true;
        }

        try (Stream<Path> pathStream = Files.list(path)) {
            return !pathStream.findAny().isPresent();
        }
    }

    /**
     * Copy the contents of the given input File to the given output File.
     *
     * @param in  the file to copy from
     * @param out the file to copy to
     * @return the number of bytes copied
     * @throws IOException in case of I/O errors
     */
    public static int copy(File in, File out) throws IOException {
        Assert.notNull(in, "No input File specified");
        Assert.notNull(out, "No output File specified");
        return copy(Files.newInputStream(in.toPath()), Files.newOutputStream(out.toPath()));
    }

    /**
     * Copy the contents of the given byte array to the given output File.
     *
     * @param in  the byte array to copy from
     * @param out the file to copy to
     * @throws IOException in case of I/O errors
     */
    public static void copy(byte[] in, File out) throws IOException {
        Assert.notNull(in, "No input byte array specified");
        Assert.notNull(out, "No output File specified");
        copy(new ByteArrayInputStream(in), Files.newOutputStream(out.toPath()));
    }

    /**
     * Copy the contents of the given InputStream to the given OutputStream.
     * Closes both streams when done.
     *
     * @param in  the stream to copy from
     * @param out the stream to copy to
     * @return the number of bytes copied
     * @throws IOException in case of I/O errors
     */
    public static int copy(InputStream in, OutputStream out) throws IOException {
        Assert.notNull(in, "No InputStream specified");
        Assert.notNull(out, "No OutputStream specified");

        try {
            return StreamUtils.copy(in, out);
        } finally {
            try {
                in.close();
            } catch (IOException ex) {
            }
            try {
                out.close();
            } catch (IOException ex) {
            }
        }
    }

    /**
     * Copy the contents of the given byte array to the given OutputStream.
     * Closes the stream when done.
     *
     * @param in  the byte array to copy from
     * @param out the OutputStream to copy to
     * @throws IOException in case of I/O errors
     */
    public static void copy(byte[] in, OutputStream out) throws IOException {
        Assert.notNull(in, "No input byte array specified");
        Assert.notNull(out, "No OutputStream specified");

        try {
            out.write(in);
        } finally {
            try {
                out.close();
            } catch (IOException ex) {
            }
        }
    }

    /**
     * 将文件保存至本地
     */
    public static void saveFileToLocal(MultipartFile file, String localPath) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be null or empty!");
        }
        // 获取文件输入流
        try (InputStream inputStream = file.getInputStream()) {
            // 构造文件路径
            Path targetLocation = Paths.get(localPath);
            // 保存文件
            Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
            // 如果需要，可以在这里添加文件保存成功后的处理逻辑
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将图片文件保存成base64
     *
     * @param file
     * @return
     */
    public static String saveFileToBase64(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ""; // 或者你可以返回一个空字符串或其他表示空值的字符串
        }
        try {
            // 将文件内容写入到ByteArrayOutputStream中
            return Base64.getEncoder().encodeToString(file.getBytes());
            // 使用Base64对字节数组进行编码
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将图片文件保存成base64
     *
     * @param file
     * @return
     */
    public static String saveFileToBase64(File file) {
        if (file == null || !file.exists()) {
            return ""; // 或者你可以返回一个空字符串或其他表示空值的字符串
        }
        try {
            // 将文件内容写入到ByteArrayOutputStream中
            return Base64.getEncoder().encodeToString(Files.readAllBytes(file.toPath()));
            // 使用Base64对字节数组进行编码
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将图片文件保存成base64
     *
     * @param path 路径
     * @return
     */
    public static String saveFileToBase64(String path) {
        if (StringUtils.isBlank(path)) {
            return "";
        }
        File file = new File(path);
        if (!file.exists()) {
            return ""; // 或者你可以返回一个空字符串或其他表示空值的字符串
        }
        try {
            // 将文件内容写入到ByteArrayOutputStream中
            return Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get(path)));
            // 使用Base64对字节数组进行编码
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将base64保存至本地
     *
     * @param base64
     * @param locationPath
     * @return
     */
    public static Boolean saveBase64ToLocal(String base64, String locationPath) {
        if (StringUtils.isBlank(base64) || StringUtils.isBlank(locationPath)) {
            return false;
        }
        // 解码Base64字符串
        byte[] decodedBytes = Base64.getDecoder().decode(base64);
        // 构造文件路径
        Path path = Paths.get(locationPath);
        File file = path.toFile();
        // 如果文件所在目录不存在，则创建目录
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            if (!parentDir.mkdirs()) {
                System.err.println("无法创建目录: " + parentDir.getAbsolutePath());
                return false;
            }
        }
        // 尝试写入文件
        try (FileOutputStream fos = new FileOutputStream(locationPath)) {
            fos.write(decodedBytes);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    public static Long getVideoDuration(File file) {
        try {
            FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(file);
            grabber.start();
            double duration = grabber.getLengthInTime() / 1000000.0;
            grabber.stop();
            return (long) duration;
        } catch (Exception e) {
            return 0L;
        }

    }


    public static Long getVideoFrameCount(File file) {
        try {
            FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(file);
            grabber.start();
            int frameCount = grabber.getLengthInFrames();
            grabber.stop();
            return (long) frameCount;

        } catch (Exception e) {
            return 0L;
        }
    }

    public static void copy(String filePath, String targetPath) {
        Path source = Paths.get(filePath);
        Path destination = Paths.get(targetPath);
        try {
            Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static Integer getVideoSize(File file) {
        if (Objects.isNull(file)) {
            return 0;
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (Objects.isNull(files) || files.length == 0) {
                return 0;
            }
            int size = 0;
            for (File f : files) {
                String name = f.getName();
                if (name.startsWith(".") || name.endsWith(".tmp") || name.endsWith(".swp") || name.endsWith(".part") || name.endsWith(".cache")) {
                    continue;
                }
                if (FileUtil.judgeVideoFile(f)) {
                    size = size + 1;
                } else if (f.isDirectory()) {
                    size = size + 1;
                } else {
                    continue;
                }
            }
            return size;
        }
        return 0;
    }

    public static Integer getImageSize(File file) {
        if (Objects.isNull(file)) {
            return 0;
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (Objects.isNull(files) || files.length == 0) {
                return 0;
            }
            int size = 0;
            for (File f : files) {
                String name = f.getName();
                if (name.startsWith(".") || name.endsWith(".tmp") || name.endsWith(".swp") || name.endsWith(".part") || name.endsWith(".cache")) {
                    continue;
                }
                if (FileUtil.judgeImageFile(f)) {
                    size = size + 1;
                } else if (f.isDirectory()) {
                    size = size + 1;
                } else {
                    continue;
                }
            }
            return size;
        }
        return 0;
    }

    public static long calculateDirectorySize(File file) {
        if (file.isFile()) {
            return file.length();
        }

        if (file.isDirectory()) {
            // 使用 Stream API 并行处理（适合大目录）
            OptionalLong total = Arrays.stream(Optional.ofNullable(file.listFiles())
                            .orElse(new File[0]))
                    .parallel()
                    .mapToLong(FileUtil::calculateDirectorySize)
                    .reduce(Long::sum);

            return total.orElse(0L);
        }

        return 0L;
    }
}
 