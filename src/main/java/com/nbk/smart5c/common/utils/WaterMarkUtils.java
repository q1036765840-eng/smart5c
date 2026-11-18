package com.nbk.smart5c.common.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.nbk.smart5c.pojo.bo.DefectMarkBO;
import com.nbk.smart5c.pojo.bo.ImageDTO;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * @use 利用Java代码给图片添加文字(透明图调低点, 也可以当做水印)
 */
@Slf4j
public class WaterMarkUtils {
    /**
     * 编辑图片,往指定位置添加文字
     *
     * @param srcImgPath    :源图片路径
     * @param targetImgPath :保存图片路径
     * @param list          :文字集合
     */
    public static void writeImage(String srcImgPath, String targetImgPath, List<ImageDTO> list) {
        FileOutputStream outImgStream = null;
        try {
            //读取原图片信息
            File srcImgFile = new File(srcImgPath);//得到文件
            Image srcImg = ImageIO.read(srcImgFile);//文件转化为图片
            int srcImgWidth = srcImg.getWidth(null);//获取图片的宽
            int srcImgHeight = srcImg.getHeight(null);//获取图片的高

            //添加文字:
            BufferedImage bufImg = new BufferedImage(srcImgWidth, srcImgHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = bufImg.createGraphics();
            g.drawImage(srcImg, 0, 0, srcImgWidth, srcImgHeight, null);
            for (ImageDTO imgDTO : list) {
                g.setColor(imgDTO.getColor());                                  //根据图片的背景设置水印颜色
                g.setFont(imgDTO.getFont());                                    //设置字体
                g.drawString(imgDTO.getText(), imgDTO.getX(), imgDTO.getY());   //画出水印
            }
            g.dispose();

            // 输出图片
            outImgStream = new FileOutputStream(targetImgPath);
            ImageIO.write(bufImg, "jpg", outImgStream);
        } catch (Exception e) {
            log.error("==== 系统异常::{} ====", e.getMessage());
        } finally {
            try {
                if (null != outImgStream) {
                    outImgStream.flush();
                    outImgStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 创建ImageDTO, 每一个对象,代表在该图片中要插入的一段文字内容:
     *
     * @param text  : 文本内容;
     * @param color : 字体颜色(前三位)和透明度(第4位,值越小,越透明);
     * @param font  : 字体的样式和字体大小;
     * @param x     : 当前字体在该图片位置的横坐标;
     * @param y     : 当前字体在该图片位置的纵坐标;
     * @return
     */
    public static ImageDTO createImageDTO(String text, Color color, Font font, int x, int y) {
        ImageDTO imageDTO = new ImageDTO();
        imageDTO.setText(text);
        imageDTO.setColor(color);
        imageDTO.setFont(font);
        imageDTO.setX(x);
        imageDTO.setY(y);
        return imageDTO;
    }

    /**
     * 在图片上按指定坐标画框
     *
     * @param sourceFile 源文件
     */
    public static void drawRedRectangleToNewFile(File sourceFile, String newFilePath, List<DefectMarkBO> markList) {
        try {
            BufferedImage image = ImageIO.read(sourceFile);
            if (image == null) {
                log.error("无法读取图片文件:{}", sourceFile.getAbsolutePath());
                return;
            }
            // 确保图像是RGB类型,以便正确显示颜色
            if (image.getType() != BufferedImage.TYPE_INT_RGB && image.getType() != BufferedImage.TYPE_INT_ARGB) {
                BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
                Graphics2D g = newImage.createGraphics();
                g.drawImage(image, 0, 0, null);
                g.dispose();
                image = newImage;
            }
            if (CollUtil.isNotEmpty(markList)) {
                Graphics2D g2d = image.createGraphics();
                // 设置抗锯齿,使线条更平滑
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(255, 0, 0)); // 使用纯红色
                g2d.setStroke(new BasicStroke(4));
                for (DefectMarkBO mark : markList) {
                    List<Integer> codList = parseCoord(mark);
                    if (codList == null || codList.size() < 4) {
                        continue;
                    }
                    int x = codList.get(0);
                    int y = codList.get(1);
                    int width = codList.get(2) - x;
                    int height = codList.get(3) - y;
                    g2d.drawRect(x, y, width, height);
                }
                g2d.dispose();
            }
            ImgUtil.write(image, new File(newFilePath));
        } catch (Exception e) {
            log.error("图片绘制缺陷标记失败:{}", e.getLocalizedMessage());
        }
    }

    /**
     * 在图片上按指定坐标画框
     *
     * @param sourceFile 源文件
     */
    public static BufferedImage drawRedRectangleToPatrol(File sourceFile, List<DefectMarkBO> markList) {
        return drawRedRectangleToPatrol(sourceFile, markList, 0);
    }

    /**
     * 在图片上按指定坐标画框，可指定缩放后的最大宽度。
     * <p>
     * 源图为数千像素宽的巡检原图，整图解码后单张可达数十 MB，高并发下极易 OOM。
     * maxWidth &gt; 0 时在解码阶段降采样（不改变宽高比），框选坐标按同一比例缩放。
     *
     * @param maxWidth 缩放后的最大宽度，&lt;= 0 表示不缩放
     */
    public static BufferedImage drawRedRectangleToPatrol(File sourceFile, List<DefectMarkBO> markList, int maxWidth) {
        try {
            ScaledImage scaledImage = readScaled(sourceFile, maxWidth);
            if (scaledImage.image == null) {
                log.error("无法读取图片文件:{}", sourceFile.getAbsolutePath());
                return null;
            }
            BufferedImage image = scaledImage.image;
            float scale = scaledImage.scale;
            // 确保图像是RGB类型,以便正确显示颜色
            if (image.getType() != BufferedImage.TYPE_INT_RGB && image.getType() != BufferedImage.TYPE_INT_ARGB) {
                BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
                Graphics2D g = newImage.createGraphics();
                g.drawImage(image, 0, 0, null);
                g.dispose();
                image = newImage;
            }
            if (CollUtil.isNotEmpty(markList)) {
                Graphics2D g2d = image.createGraphics();
                // 设置抗锯齿,使线条更平滑
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(255, 0, 0)); // 使用纯红色
                g2d.setStroke(new BasicStroke(Math.max(2f, 10f * scale)));
                for (DefectMarkBO mark : markList) {
                    List<Integer> codList = parseCoord(mark);
                    if (codList == null || codList.size() < 4) {
                        continue;
                    }
                    int x = Math.round(codList.get(0) * scale);
                    int y = Math.round(codList.get(1) * scale);
                    int width = Math.round(codList.get(2) * scale) - x;
                    int height = Math.round(codList.get(3) * scale) - y;
                    g2d.drawRect(x, y, width, height);
                }
                g2d.dispose();
            }
            return image;
        } catch (Exception e) {
            log.error("图片绘制缺陷标记失败:{}", e.getLocalizedMessage());
            return null;
        }
    }

    /**
     * 读图结果，scale 为输出宽度相对原始宽度的比例，用于换算框选坐标
     */
    static class ScaledImage {
        BufferedImage image;
        float scale = 1f;
    }

    /**
     * 按最大宽度读图，超出时在解码阶段降采样（保持宽高比），避免整图解码撑爆堆内存。
     *
     * @param maxWidth 缩放后的最大宽度，&lt;= 0 表示按原始尺寸读取
     */
    static ScaledImage readScaled(File sourceFile, int maxWidth) throws IOException {
        ScaledImage result = new ScaledImage();
        if (maxWidth <= 0) {
            result.image = ImageIO.read(sourceFile);
            return result;
        }
        try (ImageInputStream iis = ImageIO.createImageInputStream(sourceFile)) {
            if (iis == null) {
                result.image = ImageIO.read(sourceFile);
                return result;
            }
            Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
            if (!readers.hasNext()) {
                result.image = ImageIO.read(sourceFile);
                return result;
            }
            ImageReader reader = readers.next();
            try {
                reader.setInput(iis, true, true);
                int srcWidth = reader.getWidth(0);
                if (srcWidth <= maxWidth) {
                    result.image = reader.read(0);
                    return result;
                }
                // 先按整数倍粗采样把解码尺寸压下来（内存主要开销在这一步），再高质量缩放到目标宽度
                ImageReadParam param = reader.getDefaultReadParam();
                int subsampling = Math.max(1, srcWidth / maxWidth);
                param.setSourceSubsampling(subsampling, subsampling, 0, 0);
                BufferedImage coarse = reader.read(0, param);
                result.image = ImgUtil.toBufferedImage(ImgUtil.scale(coarse, maxWidth / (float) coarse.getWidth()));
                result.scale = result.image.getWidth() / (float) srcWidth;
                return result;
            } finally {
                reader.dispose();
            }
        }
    }

    /**
     * 在图片上按指定坐标画框
     *
     * @param sourceFile 源文件
     */
    public static BufferedImage drawRedRectangle(File sourceFile, List<DefectMarkBO> markList) {
        return drawRedRectangle(sourceFile, markList, 0);
    }

    /**
     * 在图片上按指定坐标画框并标注缺陷类型，可指定最大宽度。
     * <p>
     * maxWidth &gt; 0 时在解码阶段降采样，框选坐标与标注字号按同一比例缩小。
     *
     * @param maxWidth 缩放后的最大宽度，&lt;= 0 表示不缩放
     */
    public static BufferedImage drawRedRectangle(File sourceFile, List<DefectMarkBO> markList, int maxWidth) {
        try {
            ScaledImage scaledImage = readScaled(sourceFile, maxWidth);
            if (scaledImage.image == null) {
                log.error("无法读取图片文件:{}", sourceFile.getAbsolutePath());
                return null;
            }
            BufferedImage image = scaledImage.image;
            float scale = scaledImage.scale;
            // 确保图像是RGB类型,以便正确显示颜色
            if (image.getType() != BufferedImage.TYPE_INT_RGB && image.getType() != BufferedImage.TYPE_INT_ARGB) {
                BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
                Graphics2D g = newImage.createGraphics();
                g.drawImage(image, 0, 0, null);
                g.dispose();
                image = newImage;
            }
            if (CollUtil.isNotEmpty(markList)) {
                Graphics2D g2d = image.createGraphics();
                // 设置抗锯齿,使线条更平滑
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(255, 0, 0));
                g2d.setStroke(new BasicStroke(Math.max(1.5f, 5f * scale)));
                for (DefectMarkBO mark : markList) {
                    List<Integer> codList = parseCoord(mark.getCoord());
                    if (codList == null || codList.size() < 4) {
                        continue;
                    }
                    int x = Math.round(codList.get(0) * scale);
                    int y = Math.round(codList.get(1) * scale);
                    int width = Math.round(codList.get(2) * scale) - x;
                    int height = Math.round(codList.get(3) * scale) - y;
                    g2d.drawRect(x, y, width, height);

                    String text = mark.getDefectType();
                    if (StrUtil.isNotBlank(text)) {
                        Font labelFont = new Font("Microsoft YaHei", Font.PLAIN, Math.max(8, Math.round(32 * scale)));
                        g2d.setFont(labelFont);
                        FontMetrics fm = g2d.getFontMetrics();
                        int textWidth = fm.stringWidth(text);
                        int textHeight = fm.getHeight();
                        int padding = Math.max(1, Math.round(4 * scale));
                        int labelWidth = textWidth + padding * 2;
                        int labelHeight = textHeight + padding * 2;

                        int labelX = x + width - labelWidth;
                        int labelY = y - labelHeight;
                        if (labelY < 0) {
                            labelY = y + height;
                        }
                        if (labelX < 0) {
                            labelX = x;
                        }

                        g2d.setColor(new Color(255, 0, 0));
                        g2d.fillRect(labelX, labelY, labelWidth, labelHeight);

                        g2d.setColor(new Color(255, 255, 255));
                        g2d.drawString(text, labelX + padding, labelY + fm.getAscent() + padding);

                        g2d.setColor(new Color(255, 0, 0));
                    }
                }
                g2d.dispose();
            }
            return image;
        } catch (Exception e) {
            log.error("图片绘制缺陷标记失败:{}", e.getLocalizedMessage(), e);
            return null;
        }
    }

    /**
     * 将 DefectMarkBO 的 coord 字段（"x1,y1,x2,y2"）解析为整数列表
     */
    private static List<Integer> parseCoord(String mark) {
        if (StrUtil.isBlank(mark)) {
            return null;
        }
        String coord = mark.trim();
        if (!coord.startsWith("[")) {
            coord = "[" + coord;
        }
        if (!coord.endsWith("]")) {
            coord = coord + "]";
        }
        return JSON.parseArray(coord, Integer.class);
    }

    /**
     * 将 DefectMarkBO 的 coord 字段（"x1,y1,x2,y2"）解析为整数列表
     */
    private static List<Integer> parseCoord(DefectMarkBO mark) {
        if (mark == null || mark.getCoord() == null || mark.getCoord().trim().isEmpty()) {
            return null;
        }
        String coord = mark.getCoord().trim();
        if (!coord.startsWith("[")) {
            coord = "[" + coord;
        }
        if (!coord.endsWith("]")) {
            coord = coord + "]";
        }
        return JSON.parseArray(coord, Integer.class);
    }

}