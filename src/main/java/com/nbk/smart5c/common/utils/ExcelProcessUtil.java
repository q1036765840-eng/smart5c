package com.nbk.smart5c.common.utils;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.hutool.poi.excel.StyleSet;
import com.nbk.smart5c.exception.ServiceException;
import com.nbk.smart5c.pojo.bo.DefectMarkBO;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.IntConsumer;

@Slf4j
@Component
public class ExcelProcessUtil {
    private static final int IMG_HEIGHT = 30;
    private static final int IMG_WIDTH = 30;
    private static final int BYTES_DEFAULT_LENGTH = 10240;
    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getNumberInstance();

    /**
     * 导出图片的最大宽度。巡检原图宽达 8000px，整图解码单张约 50MB，
     * 高并发下会直接打爆堆内存（publish 环境 -Xmx4g），必须解码时降采样。
     * 1600px 可保持缺陷框和标注文字可辨，解码内存约 3MB/张。
     */
    private static final int EXPORT_IMAGE_MAX_WIDTH = 1600;

    /**
     * 图片预处理的最大并发数。按 CPU 核数开线程在 72 核机器上会开到 144 个并发解码，
     * 这里固定小并发，避免瞬时内存占用随核数线性增长。
     */
    private static final int IMAGE_PREPARE_PARALLELISM = 4;

    public static void export(List<String> titles, List<List<Object>> baseData, String fileName, HttpServletResponse response) throws ServiceException {
        // 1.获取ExcelWriter对象
        ExcelWriter writer = ExcelUtil.getWriter(true);
        // 2.写出表头
        // 自定义标题别名
        if (CollectionUtil.isEmpty(titles)) {
            return;
        }
        // 3.定义表头单元格样式(可选)
        StyleSet style = writer.getStyleSet();
        CellStyle headCellStyle = style.getHeadCellStyle();
        // 自动换行
        headCellStyle.setWrapText(true);
        // 水平居中
        headCellStyle.setAlignment(HorizontalAlignment.CENTER);
        // 上下居中
        headCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        // 设置表头字体大小
        Font headFont = writer.createFont();
        headFont.setFontName("宋体");
        headFont.setFontHeightInPoints((short) 15); // 设置字体大小为15磅
        headCellStyle.setFont(headFont);
        // 4.定义内容单元格样式(可选)
        CellStyle cellStyle = style.getCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        // 设置字体大小
        Font font = writer.createFont();
        font.setFontName("宋体");
        cellStyle.setFont(font);
        // 5.其他设置(可选)
        // 只写出设置别名的属性
        writer.setOnlyAlias(true);
        // 冻结行
        writer.setFreezePane(0);
        // 6.写入数据 设置列宽
        writer.writeHeadRow(titles);
        writer.write(baseData);
        writer.autoSizeColumnAll();
        // 8.导出
        response.setCharacterEncoding(CharsetUtil.UTF_8);
        try {
            fileName = URLEncoder.encode(fileName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        //test.xlsx是弹出下载对话框的文件名，不能为中文，中文请自行编码
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            writer.flush(outputStream);
            writer.close();
        } catch (Exception e) {
            log.error("批量导出出错：{}", e.getMessage());
            throw new ServiceException(ResultCode.EXPORT_FAIL.getCode(), e.getMessage());
        }

    }

    public void exportContainPicture(List<String> titles, List<List<Object>> baseData, boolean hasPic, String fileName, HttpServletResponse response) throws ServiceException {
        exportContainPicture(titles, baseData, hasPic, fileName, response, null);
    }

    /**
     * 导出带图片的 Excel，并通过 progressCallback 上报整体进度（0-100）
     *
     * @param progressCallback 进度回调，可为 null
     */
    public void exportContainPicture(List<String> titles, List<List<Object>> baseData, boolean hasPic, String fileName,
                                     HttpServletResponse response, IntConsumer progressCallback) throws ServiceException {
        reportProgress(progressCallback, 0);
        // 1.获取ExcelWriter对象（xlsx：xls 单表上限 65536 行，且图片体积更大）
        ExcelWriter writer = ExcelUtil.getWriter(true);
        // 2.写出表头
        // 自定义标题别名
        if (CollectionUtil.isEmpty(titles)) {
            return;
        }
        // 3.定义表头单元格样式(可选)
        StyleSet style = writer.getStyleSet();
        CellStyle headCellStyle = style.getHeadCellStyle();
        // 自动换行
        headCellStyle.setWrapText(true);
        // 水平居中
        headCellStyle.setAlignment(HorizontalAlignment.CENTER);
        // 上下居中
        headCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        // 设置表头字体大小
        Font headFont = writer.createFont();
        headFont.setFontName("宋体");
        headFont.setFontHeightInPoints((short) 15); // 设置字体大小为15磅
        headCellStyle.setFont(headFont);
        // 4.定义内容单元格样式(可选)
        CellStyle cellStyle = style.getCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        // 设置字体大小
        Font font = writer.createFont();
        font.setFontName("宋体");
        cellStyle.setFont(font);
        // 5.其他设置(可选)
        // 只写出设置别名的属性
        writer.setOnlyAlias(true);
        // 冻结行
        writer.setFreezePane(0);
        // 6.写入数据 设置列宽
        writer.writeHeadRow(titles);
        writer.write(baseData);
        reportProgress(progressCallback, 10);
        if (hasPic) {
            // 并行预处理图片字节（IO+CPU密集），再串行写入Excel（POI非线程安全）
            List<byte[]> pictureBytesList = preparePicturesParallel(baseData, progressCallback);
            writePictures(baseData, writer, 1, pictureBytesList);
        }
        reportProgress(progressCallback, 90);
        // autoSizeColumnAll 在大数据量下极慢且对图片列无意义，改为固定列宽
        for (int i = 0; i < titles.size(); i++) {
            writer.setColumnWidth(i, hasPic && i == titles.size() - 1 ? 30 : 20);
        }
        // 8.导出
        response.setCharacterEncoding(CharsetUtil.UTF_8);
        try {
            fileName = URLEncoder.encode(fileName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error(fileName + ":文件名转码异常,{}", e.getLocalizedMessage(), e);
        }
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=" + CharsetUtil.UTF_8);
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            writer.flush(outputStream);
            writer.close();
            reportProgress(progressCallback, 100);
        } catch (Exception e) {
            // Hutool 的 flush 会把 IOException 包装成 IORuntimeException，需沿 cause 链判断
            if (ClientAbortUtil.isClientAbort(e)) {
                log.warn("客户端中断了导出请求：{}", e.getMessage());
                throw new ServiceException("客户端中断了导出请求", e);
            }
            log.error("批量导出出错：{}", e.getMessage(), e);
            throw new ServiceException(e.getMessage(), e);
        }
    }

    /**
     * 并行预处理所有图片为JPEG字节数组，返回与 baseData 等长的列表（无图片位置为 null）
     * <p>
     * 图片读取、水印绘制、缩放、JPEG编码均为IO+CPU密集操作，串行执行是导出卡顿的主因。
     * 使用固定线程池并行处理，单行异常不影响其他行。
     */
    private List<byte[]> preparePicturesParallel(List<List<Object>> baseData, IntConsumer progressCallback) {
        int size = baseData.size();
        List<byte[]> result = new ArrayList<>(Collections.nCopies(size, null));
        if (size == 0) {
            return result;
        }
        int parallelism = Math.min(size, IMAGE_PREPARE_PARALLELISM);
        ExecutorService executor = Executors.newFixedThreadPool(Math.max(parallelism, 1), r -> {
            Thread t = new Thread(r, "excel-pic-prepare");
            t.setDaemon(true);
            return t;
        });
        List<Future<Integer>> futures = new ArrayList<>(size);
        try {
            for (int i = 0; i < size; i++) {
                final int rowIdx = i;
                final List<Object> rowData = baseData.get(i);
                futures.add(executor.submit(() -> {
                    byte[] bytes = prepareSingleRowPicture(rowData);
                    if (bytes != null) {
                        result.set(rowIdx, bytes);
                    }
                    return rowIdx;
                }));
            }
            int finished = 0;
            for (Future<Integer> f : futures) {
                try {
                    f.get();
                } catch (Exception e) {
                    log.error("图片预处理失败, row={}", e.getMessage(), e);
                }
                finished++;
                // 图片预处理占总进度的 10~90%
                reportProgress(progressCallback, 10 + (int) (finished * 80L / size));
            }
        } finally {
            executor.shutdown();
        }
        return result;
    }

    /**
     * 处理单行图片：解析路径与标记 → 读取图片并画框 → 缩放 → JPEG字节
     */
    private byte[] prepareSingleRowPicture(List<Object> rowData) {
        if (CollectionUtil.isEmpty(rowData)) {
            return null;
        }
        Object pathObj = rowData.get(rowData.size() - 1);
        if (null == pathObj) {
            return null;
        }
        String imagePath = pathObj.toString();
        if (CharSequenceUtil.isBlank(imagePath)) {
            return null;
        }
        String[] imageArr = imagePath.split(";");
        File file = new File(imageArr[0]);
        if (!file.exists()) {
            return null;
        }
        List<DefectMarkBO> markList = imageArr.length > 1
                ? JSONUtil.toList(imageArr[1], DefectMarkBO.class)
                : Collections.emptyList();
        BufferedImage bufferedImage = WaterMarkUtils.drawRedRectangle(file, markList, EXPORT_IMAGE_MAX_WIDTH);
        if (null == bufferedImage) {
            return null;
        }
        return ImgUtil.toBytes(bufferedImage, "jpeg");
    }

    /**
     * 串行写入预处理好的图片字节到Excel（POI的Sheet/Drawing非线程安全）
     */
    private void writePictures(List<List<Object>> exportData, ExcelWriter writer, int intervalRows, List<byte[]> pictureBytesList) {
        if (CollectionUtil.isEmpty(exportData)) {
            return;
        }
        Drawing drawingPatriarch = writer.getSheet().createDrawingPatriarch();
        Workbook workbook = writer.getSheet().getWorkbook();
        for (int i = 0; i < exportData.size(); i++) {
            List<Object> rowData = exportData.get(i);
            byte[] pictureData = pictureBytesList.get(i);
            // 图片列先清空：无论图片是否处理成功，都不能把原始路径泄漏到报表里
            writer.writeCellValue(rowData.size() - 1, i + intervalRows, null);
            if (null == pictureData) {
                continue;
            }
            writer.setRowHeight(i + intervalRows, 120);
            writePic(writer, rowData.size() - 1, i + intervalRows, pictureData, drawingPatriarch, workbook);
        }
    }

    /**
     * @param exportData   需要导出的数据
     * @param writer       写入对象
     * @param intervalRows 间隔行数
     */
    public void processImagelink(List<List<Object>> exportData, ExcelWriter writer, int intervalRows) {
        if (CollectionUtil.isNotEmpty(exportData)) {
            List<byte[]> pictureBytesList = preparePicturesParallel(exportData, null);
            writePictures(exportData, writer, intervalRows, pictureBytesList);
        }
    }

    /**
     * @param writer
     * @param x           单元格x轴坐标
     * @param y           单元格y轴坐标
     * @param pictureData 图片二进制数据
     */
    private void writePic(ExcelWriter writer, int x, int y, byte[] pictureData) {
        Sheet sheet = writer.getSheet();
        Drawing drawingPatriarch = sheet.createDrawingPatriarch();
        writePic(writer, x, y, pictureData, drawingPatriarch, sheet.getWorkbook());
    }

    /**
     * 复用外部传入的 Drawing 与 Workbook，避免每行重复创建
     */
    private void writePic(ExcelWriter writer, int x, int y, byte[] pictureData, Drawing drawingPatriarch, Workbook workbook) {
        // 设置图片单元格位置
        ClientAnchor anchor = drawingPatriarch.createAnchor(0, 0, 0, 0, x, y, x + 1, y + 1);
        // 随单元格改变位置和大小
        anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
        // 添加图片
        int pictureIndex = workbook.addPicture(pictureData, Workbook.PICTURE_TYPE_JPEG);
        drawingPatriarch.createPicture(anchor, pictureIndex);
    }

    /**
     * 导出包含图片的压缩包（Excel+图片文件夹）
     *
     * @param titles   表头
     * @param baseData 数据
     * @param fileName 导出的ZIP文件名
     * @param response HTTP响应
     */
    public void exportWithPicturesAsZip(List<String> titles,
                                        List<List<Object>> baseData,
                                        String fileName,
                                        HttpServletResponse response) throws ServiceException {

        File tempExcelFile = null;
        File tempImagesDir = null;

        try {
            // 1. 创建临时目录
            String tempDir = System.getProperty("java.io.tmpdir");
            String uniqueId = UUID.randomUUID().toString().substring(0, 8);
            String exportDirName = "export_" + uniqueId;
            Path exportDirPath = Paths.get(tempDir, exportDirName);

            if (!Files.exists(exportDirPath)) {
                Files.createDirectories(exportDirPath);
            }
            // 4. 生成Excel文件
            String excelFileName = fileName.replace(".zip", ".xlsx");
            if (!excelFileName.endsWith(".xlsx")) {
                excelFileName += ".xlsx";
            }

            tempExcelFile = new File(exportDirPath.toFile(), excelFileName);

            // 使用Hutool创建ExcelWriter
            ExcelWriter writer = ExcelUtil.getWriter(tempExcelFile);
            // 2. 创建图片子目录
            tempImagesDir = new File(exportDirPath.toFile(), "images");
            if (!tempImagesDir.exists()) {
                tempImagesDir.mkdirs();
            }

            // 3. 处理数据，将图片保存到临时目录，并生成Excel文件
            // 注意：这里假设图片信息在最后一列
            List<List<Object>> processedData = new ArrayList<>();

            for (int i = 0; i < baseData.size(); i++) {
                List<Object> rowData = new ArrayList<>(baseData.get(i));

                // 处理图片列（假设最后一列是图片路径信息）
                int lastIndex = rowData.size() - 1;
                Object imageInfo = rowData.get(lastIndex);

                if (imageInfo != null) {
                    String imagePathInfo = imageInfo.toString();
                    if (!CharSequenceUtil.isBlank(imagePathInfo)) {
                        // 保存图片到临时目录，并生成超链接
                        saveImageAndCreateHyperlink(writer, imagePathInfo, tempImagesDir, i, lastIndex);
                        // 将单元格内容替换为超链接文本
                        rowData.set(lastIndex, "查看图片");
                    }
                }

                processedData.add(rowData);
            }

            // 设置样式（同原有export方法）
            StyleSet style = writer.getStyleSet();
            CellStyle headCellStyle = style.getHeadCellStyle();
            headCellStyle.setWrapText(true);
            headCellStyle.setAlignment(HorizontalAlignment.CENTER);
            headCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            Font headFont = writer.createFont();
            headFont.setFontName("宋体");
            headFont.setFontHeightInPoints((short) 15);
            headCellStyle.setFont(headFont);

            CellStyle cellStyle = style.getCellStyle();
            cellStyle.setAlignment(HorizontalAlignment.LEFT);
            Font font = writer.createFont();
            font.setFontName("宋体");
            cellStyle.setFont(font);

            writer.setOnlyAlias(true);
            writer.setFreezePane(0);

            // 写入数据
            writer.writeHeadRow(titles);
            writer.write(processedData);
            writer.autoSizeColumnAll();

            // 关闭writer，保存文件
            writer.close();

            // 5. 创建压缩包
            String zipFileName = fileName.endsWith(".zip") ? fileName : fileName + ".zip";

            // 准备要压缩的文件列表
            List<File> filesToZip = new ArrayList<>();
            filesToZip.add(tempExcelFile);
            filesToZip.add(tempImagesDir);

            // 6. 创建压缩包并输出到响应
            ZipProcessUtil.createZipToResponse(filesToZip, response, zipFileName);

        } catch (Exception e) {
            if (ClientAbortUtil.isClientAbort(e)) {
                log.warn("客户端中断了导出请求：{}", e.getMessage());
                throw new ServiceException("客户端中断了导出请求", e);
            }
            log.error("导出压缩包失败: {}", e.getMessage(), e);
            throw new ServiceException("导出压缩包失败: " + e.getMessage(), e);
        } finally {
            // 7. 清理临时文件
            cleanupTempFiles(tempExcelFile, tempImagesDir);
        }
    }

    /**
     * 导出包含图片的压缩包（Excel+图片文件夹），images 下图片命名为 基础名_序号（同名时序号递增，从1开始）
     *
     * @param titles           表头
     * @param baseData         数据
     * @param imageColumn      图片路径信息（path;markJson）所在列，导出后该列显示为“查看图片”超链接
     * @param imageBaseNames   与 baseData 逐行对应的图片基础名（不含序号与扩展名），为空或空白时退化为按行号命名
     * @param fileName         导出的ZIP文件名
     * @param response         HTTP响应
     * @param progressCallback 进度回调，参数为 0-100 的整体百分比，可为 null
     */
    public void exportWithPicturesAsZipNamed(List<String> titles,
                                             List<List<Object>> baseData,
                                             int imageColumn,
                                             List<String> imageBaseNames,
                                             String fileName,
                                             HttpServletResponse response,
                                             IntConsumer progressCallback) throws ServiceException {

        File tempExcelFile = null;
        File tempImagesDir = null;

        try {
            // 1. 创建临时目录
            String tempDir = System.getProperty("java.io.tmpdir");
            String uniqueId = UUID.randomUUID().toString().substring(0, 8);
            Path exportDirPath = Paths.get(tempDir, "export_" + uniqueId);
            Files.createDirectories(exportDirPath);

            // 2. Excel 与 images 子目录
            String excelFileName = fileName.replace(".zip", ".xlsx");
            if (!excelFileName.endsWith(".xlsx")) {
                excelFileName += ".xlsx";
            }
            tempExcelFile = new File(exportDirPath.toFile(), excelFileName);
            tempImagesDir = new File(exportDirPath.toFile(), "images");
            if (!tempImagesDir.exists()) {
                tempImagesDir.mkdirs();
            }

            reportProgress(progressCallback, 5);

            // 3. 并行处理图片（IO+CPU密集）：读取、画框、编码为字节，返回与行对应的处理结果
            //    POI 与磁盘写文件非线程安全，落盘/写Excel/分配序号在主线程串行完成
            int size = baseData.size();
            List<ProcessedRowImage> processed = parallelProcessRowImages(baseData, imageColumn, progressCallback);

            ExcelWriter writer = ExcelUtil.getWriter(tempExcelFile);
            // 同名基础名计数器，用于生成 _1/_2 序号
            Map<String, Integer> nameCounter = new HashMap<>();
            // 行号 -> (最终图片文件名)，写完Excel后统一补超链接
            Map<Integer, ImageInfo> imageInfoMap = new LinkedHashMap<>();

            List<List<Object>> processedData = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                List<Object> rowData = new ArrayList<>(baseData.get(i));
                ProcessedRowImage pri = processed.get(i);
                if (pri != null && pri.imageBytes != null) {
                    String baseName = imageBaseNames != null && i < imageBaseNames.size()
                            ? imageBaseNames.get(i) : null;
                    if (CharSequenceUtil.isBlank(baseName)) {
                        baseName = "img_" + i;
                    }
                    baseName = sanitizeFileName(baseName);
                    int seq = nameCounter.merge(baseName, 1, Integer::sum);
                    String extension = pri.extension;
                    String imageFileName = baseName + "_" + seq + extension;
                    File outputImageFile = new File(tempImagesDir, imageFileName);
                    try (java.io.FileOutputStream fos = new java.io.FileOutputStream(outputImageFile)) {
                        fos.write(pri.imageBytes);
                        imageInfoMap.put(i, new ImageInfo(imageFileName, "查看图片"));
                        rowData.set(imageColumn, "查看图片");
                    } catch (Exception e) {
                        log.error("保存图片失败, row={}: {}", i, e.getMessage(), e);
                    }
                }
                processedData.add(rowData);
                reportProgress(progressCallback, 80 + (int) ((i + 1) * 10L / size));
            }

            // 4. 设置样式（同原export方法）
            StyleSet style = writer.getStyleSet();
            CellStyle headCellStyle = style.getHeadCellStyle();
            headCellStyle.setWrapText(true);
            headCellStyle.setAlignment(HorizontalAlignment.CENTER);
            headCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            Font headFont = writer.createFont();
            headFont.setFontName("宋体");
            headFont.setFontHeightInPoints((short) 15);
            headCellStyle.setFont(headFont);

            CellStyle cellStyle = style.getCellStyle();
            cellStyle.setAlignment(HorizontalAlignment.LEFT);
            Font font = writer.createFont();
            font.setFontName("宋体");
            cellStyle.setFont(font);

            writer.setOnlyAlias(true);
            writer.setFreezePane(0);

            writer.writeHeadRow(titles);
            writer.write(processedData);
            // 大数据量下 autoSizeColumnAll 极慢，改为固定列宽
            // 注意：writer.setColumnWidth 入参是字符数（内部*256），此处直接用 POI 原生 API（单位 1/256 字符）
            for (int i = 0; i < titles.size(); i++) {
                writer.getSheet().setColumnWidth(i, i == imageColumn ? 20 * 256 : 10 * 256);
            }

            // 5. 写完数据后统一添加超链接（写入前添加会被 writer.write 覆盖）
            for (Map.Entry<Integer, ImageInfo> entry : imageInfoMap.entrySet()) {
                addHyperlinkToExcel(writer, entry.getKey() + 1, imageColumn,
                        entry.getValue().hyperlinkText, entry.getValue().fileName, tempImagesDir);
            }

            writer.close();
            reportProgress(progressCallback, 95);

            // 6. 创建压缩包并输出到响应
            String zipFileName = fileName.endsWith(".zip") ? fileName : fileName + ".zip";
            List<File> filesToZip = new ArrayList<>();
            filesToZip.add(tempExcelFile);
            filesToZip.add(tempImagesDir);
            ZipProcessUtil.createZipToResponse(filesToZip, response, zipFileName);

            reportProgress(progressCallback, 100);
        } catch (Exception e) {
            if (ClientAbortUtil.isClientAbort(e)) {
                log.warn("客户端中断了导出请求：{}", e.getMessage());
                throw new ServiceException("客户端中断了导出请求", e);
            }
            log.error("导出压缩包失败: {}", e.getMessage(), e);
            throw new ServiceException("导出压缩包失败: " + e.getMessage(), e);
        } finally {
            cleanupTempFiles(tempExcelFile, tempImagesDir);
        }
    }

    private static void reportProgress(IntConsumer callback, int progress) {
        if (callback == null) {
            return;
        }
        try {
            callback.accept(progress);
        } catch (Exception e) {
            log.warn("导出进度回调失败: {}", e.getMessage());
        }
    }

    /**
     * 单行图片的并行处理结果
     */
    private static class ProcessedRowImage {
        byte[] imageBytes;
        String extension;
    }

    /**
     * 并行处理所有行的图片（读取文件、画框、编码），返回与 baseData 等长的列表（无图/失败位置为 null）
     * 编码统一为 jpg，避免按原始扩展名分支，也规避 ImageIO 对特殊格式支持问题
     */
    private List<ProcessedRowImage> parallelProcessRowImages(List<List<Object>> baseData, int imageColumn, IntConsumer progressCallback) {
        int size = baseData.size();
        List<ProcessedRowImage> result = new ArrayList<>(Collections.nCopies(size, null));
        if (size == 0) {
            return result;
        }
        // 压缩包内保留原图分辨率，单张解码可达数十 MB，并发数按核数放大会直接 OOM
        int parallelism = Math.min(size, IMAGE_PREPARE_PARALLELISM);
        ExecutorService executor = Executors.newFixedThreadPool(Math.max(parallelism, 1), r -> {
            Thread t = new Thread(r, "zip-pic-prepare");
            t.setDaemon(true);
            return t;
        });
        List<Future<Integer>> futures = new ArrayList<>(size);
        try {
            for (int i = 0; i < size; i++) {
                final int rowIdx = i;
                final String imageInfo = extractImageInfo(baseData.get(i), imageColumn);
                futures.add(executor.submit(() -> {
                    if (CharSequenceUtil.isBlank(imageInfo)) {
                        return rowIdx;
                    }
                    String[] imageArr = imageInfo.split(";");
                    File file = new File(imageArr[0]);
                    if (!file.exists()) {
                        return rowIdx;
                    }
                    List<DefectMarkBO> markList = imageArr.length > 1
                            ? JSONUtil.toList(imageArr[1], DefectMarkBO.class)
                            : Collections.emptyList();
                    BufferedImage bufferedImage = WaterMarkUtils.drawRedRectangleToPatrol(file, markList);
                    if (bufferedImage == null) {
                        return rowIdx;
                    }
                    ProcessedRowImage pri = new ProcessedRowImage();
                    pri.imageBytes = ImgUtil.toBytes(bufferedImage, "jpg");
                    pri.extension = ".jpg";
                    result.set(rowIdx, pri);
                    return rowIdx;
                }));
            }
            int finished = 0;
            for (Future<Integer> f : futures) {
                try {
                    f.get();
                } catch (Exception e) {
                    log.error("图片并行处理失败: {}", e.getMessage(), e);
                }
                finished++;
                reportProgress(progressCallback, 5 + (int) (finished * 75L / size));
            }
        } finally {
            executor.shutdown();
        }
        return result;
    }

    private static String extractImageInfo(List<Object> rowData, int imageColumn) {
        if (CollectionUtil.isEmpty(rowData) || imageColumn < 0 || imageColumn >= rowData.size()) {
            return null;
        }
        Object pathObj = rowData.get(imageColumn);
        return pathObj == null ? null : pathObj.toString();
    }

    /**
     * 去除文件名中的非法字符与路径分隔符
     */
    private static String sanitizeFileName(String name) {
        if (CharSequenceUtil.isBlank(name)) {
            return "img";
        }
        return name.replaceAll("[\\\\/:*?\"<>|\\s]", "_");
    }

    /**
     * 保存图片并创建超链接
     */
    private static void saveImageAndCreateHyperlink(ExcelWriter writer, String imagePathInfo, File imagesDir, int row, int column) {
        try {
            // 解析图片路径和标记（与原逻辑相同）
            String[] imageArr = imagePathInfo.split(";");
            String originalImagePath = imageArr[0];
            List<DefectMarkBO> markList = new ArrayList<>();
            if (imageArr.length > 1) {
                markList = JSONUtil.toList(imageArr[1], DefectMarkBO.class);
            }

            // 处理图片，添加水印
            File file = new File(originalImagePath);
            BufferedImage bufferedImage = WaterMarkUtils.drawRedRectangleToPatrol(file, markList);
            if (bufferedImage == null) {
                return;
            }

            // 生成唯一文件名
            String originalName = new File(originalImagePath).getName();
            String extension = originalName.contains(".") ?
                    originalName.substring(originalName.lastIndexOf(".")) : ".jpg";
            String imageFileName = "img_" + row + "_" + System.currentTimeMillis() + extension;

            // 保存图片到临时目录
            File outputImageFile = new File(imagesDir, imageFileName);
            ImageIO.write(bufferedImage, extension.replace(".", ""), outputImageFile);

            addHyperlinkToExcel(writer, (row + 1), column, "查看图片", imageFileName, imagesDir);
        } catch (Exception e) {
            log.error("保存图片失败: {}", e.getMessage());
        }
    }

    /**
     * 清理临时文件
     */
    private static void cleanupTempFiles(File... files) {
        for (File file : files) {
            if (file != null && file.exists()) {
                try {
                    if (file.isDirectory()) {
                        // 删除目录及其内容
                        deleteDirectory(file);
                    } else {
                        Files.deleteIfExists(file.toPath());
                    }
                } catch (Exception e) {
                    log.warn("删除临时文件失败: {}", file.getAbsolutePath(), e);
                }
            }
        }
    }

    /**
     * 递归删除目录
     */
    private static void deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            File[] children = dir.listFiles();
            if (children != null) {
                for (File child : children) {
                    deleteDirectory(child);
                }
            }
        }
        try {
            Files.deleteIfExists(dir.toPath());
        } catch (Exception e) {
            log.warn("删除目录失败: {}", dir.getAbsolutePath(), e);
        }
    }

    private static class ImageInfo {
        String fileName;
        String hyperlinkText;

        ImageInfo(String fileName, String hyperlinkText) {
            this.fileName = fileName;
            this.hyperlinkText = hyperlinkText;
        }
    }

    // 在生成Excel时创建超链接

    /**
     * 在Excel中添加超链接
     */
    private static void addHyperlinkToExcel(ExcelWriter writer, int row, int column,
                                            String linkText, String imageFileName, File imagesDir) {
        try {
            // 获取或创建单元格
            Cell cell = writer.getOrCreateCell(column, row);

            // 创建超链接样式
            CellStyle hyperlinkStyle = writer.getWorkbook().createCellStyle();
            Font hyperlinkFont = writer.getWorkbook().createFont();
            hyperlinkFont.setUnderline(Font.U_SINGLE);
            hyperlinkFont.setColor(IndexedColors.BLUE.getIndex());
            hyperlinkStyle.setFont(hyperlinkFont);
            cell.setCellStyle(hyperlinkStyle);

            // 创建超链接
            CreationHelper createHelper = writer.getWorkbook().getCreationHelper();

            // 使用文件URI格式创建超链接
            // 注意：Excel文件超链接需要完整的文件路径
//            File imageFile = new File(imagesDir, imageFileName);
//            String filePath = imageFile.getAbsolutePath();

            // 处理Windows路径分隔符
//            filePath = filePath.replace("\\", "/");

            // 创建文件超链接
            // HyperlinkType.FILE 用于链接本地文件
            Hyperlink hyperlink = createHelper.createHyperlink(HyperlinkType.FILE);

            // 设置超链接地址
            // 注意：Excel中的文件超链接格式可能是 "file:///" + 文件路径
            hyperlink.setAddress(imagesDir.getName() + File.separator + imageFileName);

            // 设置显示文本
            cell.setCellValue(linkText);
            // 设置超链接
            cell.setHyperlink(hyperlink);

            // 添加注释（可选），说明图片位置
            if (cell.getCellComment() == null) {
                Drawing<?> drawing = writer.getSheet().createDrawingPatriarch();
                ClientAnchor anchor = createHelper.createClientAnchor();
                anchor.setCol1(column);
                anchor.setCol2(column + 3);
                anchor.setRow1(row);
                anchor.setRow2(row + 3);

                Comment comment = drawing.createCellComment(anchor);
                RichTextString commentText = createHelper.createRichTextString(
                        "图片文件: " + imageFileName + "\n" +
                                "保存路径: " + imagesDir.getAbsolutePath()
                );
                comment.setString(commentText);
                cell.setCellComment(comment);
            }

            // 设置合适的列宽
            writer.getSheet().setColumnWidth(column, 20 * 256); // 20个字符宽度

        } catch (Exception e) {
            log.error("创建Excel超链接失败: {}", e.getMessage(), e);

            // 如果创建超链接失败，至少显示文件名
            Cell cell = writer.getOrCreateCell(column, row);
            cell.setCellValue("图片: " + imageFileName);
        }
    }
}

