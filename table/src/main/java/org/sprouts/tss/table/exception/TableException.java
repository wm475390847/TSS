package org.sprouts.tss.table.exception;

import lombok.EqualsAndHashCode;

/**
 * 异常信息
 *
 * @author wangmin
 * @date 2022/8/17 10:22
 */
@EqualsAndHashCode(callSuper = true)
public class TableException extends RuntimeException {

    public TableException(String message) {
        super("表格处理错误: [" + message + "]");
    }
}
