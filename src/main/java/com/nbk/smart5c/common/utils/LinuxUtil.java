package com.nbk.smart5c.common.utils;

import com.nbk.smart5c.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.util.EnumSet;
import java.util.Set;

/**
 * 文件删除与权限工具（NIO 实现，路径必须在 inspect.basepath 之内，防止命令注入与任意路径删除）
 */
@Slf4j
public class LinuxUtil {

    private static final Set<PosixFilePermission> PERMS_777 = EnumSet.of(
            PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE, PosixFilePermission.OWNER_EXECUTE,
            PosixFilePermission.GROUP_READ, PosixFilePermission.GROUP_WRITE, PosixFilePermission.GROUP_EXECUTE,
            PosixFilePermission.OTHERS_READ, PosixFilePermission.OTHERS_WRITE, PosixFilePermission.OTHERS_EXECUTE);

    private LinuxUtil() {
    }

    public static void chmodFile(String filePath) throws ServiceException {
        chmodFile(filePath, true);
    }

    public static void chmodFile(String filePath, boolean recursive) throws ServiceException {
        PathCheckUtil.requireUnderBase(BasePathHolder.get(), filePath);
        Path path = Paths.get(filePath);
        if (!Files.exists(path) || Files.isSymbolicLink(path)) {
            return;
        }
        try {
            Files.setPosixFilePermissions(path, PERMS_777);
            log.info("权限设置成功:{}", filePath);
        } catch (UnsupportedOperationException e) {
            // 非 POSIX 文件系统（如 Windows）跳过
        } catch (IOException e) {
            log.error("修改文件权限失败:{}", e.getLocalizedMessage());
        }
        if (recursive && Files.isDirectory(path)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
                for (Path child : stream) {
                    chmodFile(child.toString(), true);
                }
            } catch (IOException e) {
                log.error("修改文件权限失败:{}", e.getLocalizedMessage());
            }
        }
    }

    public static void rmFile(String filePath) throws ServiceException {
        PathCheckUtil.requireUnderBase(BasePathHolder.get(), filePath);
        Path root = Paths.get(filePath);
        if (!Files.exists(root)) {
            return;
        }
        try {
            Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    throw exc;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    if (exc != null) {
                        throw exc;
                    }
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
            log.info("删除成功:{}", filePath);
        } catch (IOException e) {
            log.error("删除文件失败:{}", e.getLocalizedMessage());
            throw new ServiceException(520, "删除文件出错:" + e.getMessage());
        }
    }
}
