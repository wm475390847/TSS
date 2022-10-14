package com.chaohu.conner.exception;

import lombok.EqualsAndHashCode;

/**
 * 异常信息
 *
 * @author wangmin
 * @date 2022/8/17 10:22
 */
@EqualsAndHashCode(callSuper = true)
public class HttpException extends RuntimeException {

    public HttpException(String message) {
        super("http请求错误: [" + message + "]");
    }
}
