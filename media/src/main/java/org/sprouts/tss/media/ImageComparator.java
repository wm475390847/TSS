package org.sprouts.tss.media;

import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * 图像比较器
 *
 * @author wangmin
 */
@Slf4j
public class ImageComparator extends BaseMedia<Double> {
    private final String targetImagePath;
    private final String baseImagePath;
    private final String outputPath;

    public ImageComparator(Builder builder) {
        this.targetImagePath = builder.targetImagePath;
        this.baseImagePath = builder.baseImagePath;
        this.outputPath = builder.outputPath;
    }

    @Override
    public Double compare() {
        File targetImageFile = new File(targetImagePath);
        File baseImageFile = new File(baseImagePath);
        double similarity = 0.0;
        try {
            // 读取图像文件为BufferedImage对象
            BufferedImage targetImage = ImageIO.read(targetImageFile);
            BufferedImage baseImage = ImageIO.read(baseImageFile);

            // 获取图像的宽度和高度
            int width1 = targetImage.getWidth();
            int height1 = targetImage.getHeight();
            int width2 = baseImage.getWidth();
            int height2 = baseImage.getHeight();

            // 计算调整后的宽度和高度
            int width = Math.min(width1, width2);
            int height = Math.min(height1, height2);

            // 创建一个新的BufferedImage来存储对比结果
            BufferedImage resultImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            // 比较两张图片的每个像素
            int difference = 0;
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    // 获取两张图片在相同位置的像素值
                    int pixel1 = targetImage.getRGB(x, y);
                    int pixel2 = baseImage.getRGB(x, y);

                    // 计算像素的差值
                    int redDiff = Math.abs((pixel1 >> 16) & 0xFF - (pixel2 >> 16) & 0xFF);
                    int greenDiff = Math.abs((pixel1 >> 8) & 0xFF - (pixel2 >> 8) & 0xFF);
                    int blueDiff = Math.abs(pixel1 & 0xFF - pixel2 & 0xFF);

                    // 计算像素差值的平均值
                    int pixelDiff = (redDiff + greenDiff + blueDiff) / 3;

                    // 累加像素差值
                    difference += pixelDiff;

                    // 设置对比结果图像中相应像素的颜色，以差值作为依据
                    int diffColor = (255 << 24) | (pixelDiff << 16) | (pixelDiff << 8) | pixelDiff;
                    resultImage.setRGB(x, y, diffColor);
                }
            }

            // 计算平均差值
            double averageDifference = (double) difference / (width * height);

            // 输出相似度
            similarity = (1 - averageDifference / 255) * 100;
            log.info("图片相似度: {}", similarity);

            // 三张图片横向排列
            int canvasWidth = width * 3;
            // 创建一个新的 BufferedImage 来存储对比结果和原始图片的拼接
            BufferedImage canvas = new BufferedImage(canvasWidth, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = canvas.createGraphics();

            // 在画布上绘制 baseImage，位置为 (0, 0)
            g2d.drawImage(baseImage, 0, 0, null);

            // 在画布上绘制 targetImage，位置为 (width, 0)
            g2d.drawImage(targetImage, width, 0, null);

            // 在画布上绘制 resultImage，位置为 (2 * width, 0)
            g2d.drawImage(resultImage, 2 * width, 0, null);

            // 关闭 Graphics2D 对象
            g2d.dispose();

            // 将对比结果图像保存到文件
            File resultImageFile = new File(outputPath);
            try {
                ImageIO.write(canvas, "png", resultImageFile);
                log.info("对比结果图片生成成功！");
            } catch (IOException e) {
                log.info("保存对比结果图片时出现错误: {}", e.getMessage());
            }
        } catch (IOException e) {
            log.info("读取图像文件时出现错误: {}", e.getMessage());
        }
        return similarity;
    }

    @Setter
    @Accessors(chain = true, fluent = true)
    public static class Builder {
        private String targetImagePath;
        private String baseImagePath;
        private String outputPath;

        public ImageComparator build() {
            return new ImageComparator(this);
        }
    }
}
