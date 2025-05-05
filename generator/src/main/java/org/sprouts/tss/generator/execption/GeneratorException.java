package org.sprouts.tss.generator.execption;

import lombok.EqualsAndHashCode;

/**
 * 异常信息
 *
 * @author wangmin
 * @date 2022/8/17 10:22
 */
@EqualsAndHashCode(callSuper = true)
public class GeneratorException extends RuntimeException {

    public GeneratorException(String message) {
        super("生成器错误: [" + message + "]");
    }
}
