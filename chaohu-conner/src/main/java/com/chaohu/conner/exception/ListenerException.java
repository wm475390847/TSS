package com.chaohu.conner.exception;

import lombok.EqualsAndHashCode;

/**
 * 异常信息
 *
 * @author wangmin
 * @date 2022/8/17 10:22
 */
@EqualsAndHashCode(callSuper = true)
public class ListenerException extends RuntimeException {

    public ListenerException(String message) {
        super("监听异常: [" + message + "]");
    }
}
