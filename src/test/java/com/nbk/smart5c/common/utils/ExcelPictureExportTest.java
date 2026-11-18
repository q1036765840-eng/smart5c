package com.nbk.smart5c.common.utils;

import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import com.nbk.smart5c.pojo.bo.DefectMarkBO;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * 回归：publish 环境 4G 堆下导出巡检报表时，8000px 宽的原图并发解码触发 OOM，
 * 失败行在“机车图”列残留原始路径而没有图片。
 */
public class ExcelPictureExportTest {

    private static final int LINE_COUNT = 100;
    private static File srcImage;

    @BeforeClass
    public static void createLargeImage() throws Exception {
        // 与现场原图同量级：8000x2046，解码约 50MB
        BufferedImage image = new BufferedImage(8000, 2046, BufferedImage.TYPE_INT_RGB);
        srcImage = File.createTempFile("smart5c-export-test", ".jpg");
        ImageIO.write(image, "jpg", srcImage);
        srcImage.deleteOnExit();
    }

    @AfterClass
    public static void cleanUp() {
        if (srcImage != null) {
            srcImage.delete();
        }
    }

    @Test
    public void readScaledShouldDownsampleKeepingAspectRatio() throws Exception {
        WaterMarkUtils.ScaledImage scaled = WaterMarkUtils.readScaled(srcImage, 1600);
        assertNotNull(scaled.image);
        assertEquals(1600, scaled.image.getWidth());
        assertEquals(2046f / 8000f, scaled.image.getHeight() / (float) scaled.image.getWidth(), 0.01f);
        assertEquals(scaled.image.getWidth() / 8000f, scaled.scale, 0.001f);
    }

    @Test
    public void exportShouldEmbedPictureOnEveryRow() throws Exception {
        String imageInfo = srcImage.getAbsolutePath() + ";" + JSONUtil.toJsonStr(
                Lists.newArrayList(new DefectMarkBO().setCoord("1731,3079,1888,3297").setDefectType("异物")));

        List<String> titles = Arrays.asList("机车号", "检测时间", "支柱号", "分析状态", "机车图");
        List<List<Object>> rows = new ArrayList<>();
        for (int i = 0; i < LINE_COUNT; i++) {
            rows.add(Lists.newArrayList("CR400BF3125", "2026-11-04 10:00:00", "131", "已分析", imageInfo));
        }

        MockHttpServletResponse response = new MockHttpServletResponse();
        new ExcelProcessUtil().exportContainPicture(titles, rows, true, "data.xlsx", response, null);

        byte[] bytes = response.getContentAsByteArray();
        try (Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(bytes))) {
            assertEquals(LINE_COUNT, wb.getSheetAt(0).getLastRowNum());
            assertEquals(LINE_COUNT, wb.getAllPictures().size());
            for (int i = 1; i <= LINE_COUNT; i++) {
                String cell = wb.getSheetAt(0).getRow(i).getCell(4).getStringCellValue();
                assertFalse("第 " + i + " 行仍在输出原始路径", cell.contains(srcImage.getAbsolutePath()));
            }
        }
    }

    @Test
    public void missingImageShouldNotLeakPath() throws Exception {
        List<String> titles = Arrays.asList("机车号", "机车图");
        List<List<Object>> rows = new ArrayList<>();
        rows.add(Lists.newArrayList("CR400BF3125", "/not/exists/pic.jpg"));

        MockHttpServletResponse response = new MockHttpServletResponse();
        new ExcelProcessUtil().exportContainPicture(titles, rows, true, "data.xlsx", response, null);

        try (Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(response.getContentAsByteArray()))) {
            String cell = wb.getSheetAt(0).getRow(1).getCell(1).getStringCellValue();
            assertTrue("图片缺失时单元格应为空，实际为: " + cell, cell.isEmpty());
        }
    }
}
