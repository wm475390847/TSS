package com.chaohu.media;

import lombok.Getter;

/**
 * @author wangmin
 * @date 2022/10/20 09:54
 */
public enum ErrorEnum {

    /**
     * 一些错误类型
     */
    IMG_READ_ERROR(1001, "图片读取错误"),
    IMG_SIZE_ERROR(1002, "基础图片与目标图片大小不一致");

    ErrorEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Getter
    private final String message;
    @Getter
    private final Integer code;
}
