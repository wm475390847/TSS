package com.chaohu.media;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.File;

/**
 * @author wangmin
 * @date 2022/8/12 11:48
 */
@Data
@Accessors(chain = true)
public class CompareResultDTO {

    /**
     * 对比结果图片
     */
    private File diffResultImg;

    /**
     * 对比结果图片路径
     */
    private String diffResultImgPath;

    /**
     * 对比结果
     */
    private Double resultValue;

    /**
     * 信息
     */
    private String message;

    /**
     * 对比是否成功
     */
    private Boolean success;

    /**
     * 状态码
     */
    private Integer code;
}
