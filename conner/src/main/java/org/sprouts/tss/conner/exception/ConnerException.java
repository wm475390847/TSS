package org.sprouts.tss.conner.exception;

import lombok.EqualsAndHashCode;

/**
 * 异常信息
 *
 * @author wangmin
 * @date 2022/8/17 10:22
 */
@EqualsAndHashCode(callSuper = true)
public class ConnerException extends RuntimeException {

    public ConnerException(String message) {
        super("测试异常: [" + message + "]");
    }

    public ConnerException(String requestId, String message) {
        super("requestId: [" + requestId + "] 测试异常: [" + message + "]");
    }

    public ConnerException(String message, Throwable e) {
        super(message, e);
    }
}
