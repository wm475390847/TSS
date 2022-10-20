package com.chaohu.media;

import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.MatVector;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.Scalar;

import java.io.*;

import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_core.merge;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imwrite;
import static org.bytedeco.opencv.global.opencv_imgproc.*;
import static org.bytedeco.opencv.global.opencv_imgproc.dilate;

/**
 * @author wangmin
 * @date 2022/10/19 14:16
 */
@Slf4j
public class ImageCompare {
    /**
     * 图片比较
     *
     * @param targetImagePath 目标图片地址
     * @param baseImagePath   基础图片地址
     * @param outputPath      文件输出位置
     * @return 结果对象
     */
    public CompareResultDTO compareImage(String targetImagePath, String baseImagePath, String outputPath,
                                         Double strValue, Double endValue) {
        outputPath = System.getProperty("user.dir") + "/" + outputPath.replace(".", "_result" + ".");
        // 读取图片到数组
        log.info("开始读取图片");
        log.info("基础图片路径: {}", baseImagePath);
        log.info("目标图片路径: {}", targetImagePath);
        log.info("对比图片路径: {}", outputPath);
        CompareResultDTO compareResultDTO = new CompareResultDTO();
        Mat targetImage;
        Mat baseImage;
        try {
            targetImage = imread(targetImagePath);
            baseImage = imread(baseImagePath);
        } catch (Exception e) {
            return compareResultDTO.setMessage(ErrorEnum.IMG_READ_ERROR.getMessage())
                    .setCode(ErrorEnum.IMG_READ_ERROR.getCode())
                    .setSuccess(false);
        }
        log.info("图片读取成功");
        if (targetImage.size().width() != baseImage.size().width()) {
            return compareResultDTO.setMessage(ErrorEnum.IMG_SIZE_ERROR.getMessage())
                    .setCode(ErrorEnum.IMG_SIZE_ERROR.getCode())
                    .setSuccess(false);
        }

        // 等高处理
        if (targetImage.size().height() != baseImage.size().height()) {
            if (targetImage.size().height() > baseImage.size().height()) {
                targetImage = dealLongImage(targetImage.clone(), baseImage.clone());
            } else {
                baseImage = dealLongImage(baseImage.clone(), targetImage.clone());
            }
        }
        // 进行图片差异对比
        Mat imageDiff = compareImage(targetImage, baseImage);
        double nonZeroPercent = 100 * (double) countNonZero(imageDiff) / (imageDiff.size().height() * imageDiff.size().width());
        log.info("图片差异度: {}", nonZeroPercent + " %");
        if (nonZeroPercent >= strValue && nonZeroPercent <= endValue) {
            compareResultDTO.setMessage("图片一致");
        } else {
            compareResultDTO.setMessage("差异度超过 [" + strValue + "~" + endValue + "] 区间");
        }
        // 展示图片，将标准图，对比图，差异图，拼接成一张大图。其中差异图会用绿色标出差异的部分。
        set3ImageTo1(targetImage, baseImage, showDiff(imageDiff, baseImage), outputPath);
        imageDiff.release();
        baseImage.release();
        targetImage.release();
        return compareResultDTO.setDiffResultImg(new File(outputPath))
                .setDiffResultImgPath(outputPath).setResultValue(nonZeroPercent);
    }

    /**
     * 2、截取算法
     * <p>
     * a、因为图片有通用的顶部bar和底部bar，需要先找到底部bar
     * <p>
     * b、截取长图片的部分，然后和底部bar拼接，就完成了图片截取
     * <p>
     * c、这里设置一个默认的宽度，然后对比，找到相同部分，就是底部bar
     *
     * @return bar的高度
     */
    private int interceptBarHeight(Mat longImage, Mat shortImage) {
        // 设置的默认高度。
        int imageSearchMaxHeight = 400;
        Mat subImageLong = new Mat(longImage, new Rect(0, longImage.size().height() - imageSearchMaxHeight, longImage.size().width(), imageSearchMaxHeight));
        Mat subImageShort = new Mat(shortImage, new Rect(0, shortImage.size().height() - imageSearchMaxHeight, shortImage.size().width(), imageSearchMaxHeight));
        Mat imageDiff = compareImage(subImageLong, subImageShort);
        for (int row = imageDiff.size().height() - 1; row > -1; row--) {
            for (int col = 0; col < imageDiff.size().width(); col++) {
                BytePointer bytePointer = imageDiff.ptr(row, col);
                if (bytePointer.get(0) != 0) {
                    imageDiff.release();
                    return imageSearchMaxHeight - row;
                }
            }
        }
        return imageSearchMaxHeight;
    }

    /**
     * 这里将两张图片作为参数传入，获取到共同的底部之后。对长图进行截取，然后将顶部和底部拼接在一起就ok了
     *
     * @param longImage  长图
     * @param shortImage 短图
     * @return Mat
     */
    private Mat dealLongImage(Mat longImage, Mat shortImage) {
        int diffHeight = longImage.size().height() - shortImage.size().height();
        log.info("高度差异:{}", diffHeight);
        int barHeight = interceptBarHeight(longImage, shortImage);
        Mat dealLongImage = new Mat(longImage, new Rect(0, 0, longImage.size().width(), shortImage.size().height() - barHeight));
        Mat imageBar = new Mat(longImage, new Rect(0, longImage.size().height() - barHeight, longImage.size().width(), barHeight));
        Mat dealLongImageNew = dealLongImage.clone();
        // 将头部和底部bar拼接在一起。
        vconcat(dealLongImage, imageBar, dealLongImageNew);
        imageBar.release();
        dealLongImage.release();
        return dealLongImageNew;
    }


    /**
     * 比较图片算法
     *
     * @param targetImage 目标图片
     * @param baseImage   基础图片
     * @return Mat
     */
    private Mat compareImage(Mat targetImage, Mat baseImage) {
        Mat targetImageClone = targetImage.clone();
        Mat baseImageClone = baseImage.clone();
        Mat imgDiff1 = targetImage.clone();
        Mat imgDiff = targetImage.clone();
        // 首先将图片转成灰度图
        cvtColor(targetImage, targetImageClone, COLOR_BGR2GRAY);
        cvtColor(baseImage, baseImageClone, COLOR_BGR2GRAY);
        // 两个矩阵相减，获得差异图
        subtract(targetImageClone, baseImageClone, imgDiff1);
        subtract(baseImageClone, targetImageClone, imgDiff);
        // 按比重进行叠加
        addWeighted(imgDiff, 1, imgDiff1, 1, 0, imgDiff);
        // 图片二值化，大于24的为1，小于24的为0
        threshold(imgDiff, imgDiff, 24, 255, THRESH_BINARY);
        erode(imgDiff, imgDiff, new Mat());
        dilate(imgDiff, imgDiff, new Mat());
        return imgDiff;
    }

    /**
     * 将三张图片合成为一张图片
     *
     * @param targetImage 目标图片
     * @param baseImage   基础图片
     * @param diffImage   对比图片
     * @param outputPath  输出路径
     */
    private void set3ImageTo1(Mat targetImage, Mat baseImage, Mat diffImage, String outputPath) {
        if (targetImage.size().width() == diffImage.size().width() && baseImage.size().height() == diffImage.size().height()) {
            Mat targetImg = targetImage.clone();
            Mat baseImg = baseImage.clone();
            Mat diffImg = diffImage.clone();
            Mat imgLine = new Mat(baseImg.size().height(), 1, CV_8UC3, new Scalar(0, 0, 0, 255));
            Mat largeImg2 = new Mat();
            Mat largeImg3 = new Mat();
            Mat largeImg4 = new Mat();
            Mat largeImg5 = new Mat();
            // 横向拼接
            hconcat(targetImg, imgLine, largeImg2);
            hconcat(largeImg2, baseImg, largeImg3);
            hconcat(largeImg3, imgLine, largeImg4);
            hconcat(largeImg4, diffImg, largeImg5);
            // 直接输出到根目录下
            imwrite(outputPath, largeImg5);
            targetImg.release();
            baseImg.release();
            diffImg.release();
            imgLine.release();
            largeImg2.release();
            largeImg3.release();
            largeImg4.release();
            largeImg5.release();
        } else {
            log.error("图片合并失败");
            imwrite(outputPath, diffImage);
        }
    }

    private Mat showDiff(Mat diffImg, Mat baseImg) {
        MatVector rgbFrame = new MatVector();
        Mat imgDest = baseImg.clone();
        split(baseImg, rgbFrame);
        subtract(rgbFrame.get(2), diffImg, rgbFrame.get(2));
        subtract(rgbFrame.get(0), diffImg, rgbFrame.get(0));
        addWeighted(rgbFrame.get(1), 1, diffImg, 1, 0, rgbFrame.get(1));
        merge(rgbFrame, imgDest);
        return imgDest;
    }
}
