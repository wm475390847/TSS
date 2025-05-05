package org.sprouts.tss.generator.enums;

import org.sprouts.tss.generator.execption.GeneratorException;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

/**
 * 文件类型枚举
 *
 * @author wangmin
 * @date 2022/5/17 13:05
 */
@Getter
public enum FileFormatEnum {
    /**
     * java
     */
    JAVA(".java"),

    HTML(".html"),

    XML(".xml"),
    ;

    FileFormatEnum(String suffix) {
        this.suffix = suffix;
    }

    private final String suffix;

    public static FileFormatEnum findBySuffix(String suffix) {
        Optional.ofNullable(suffix).orElseThrow(() -> new GeneratorException("suffix不能为空"));
        Optional<FileFormatEnum> any = Arrays.stream(values()).filter(e -> e.getSuffix().equals(suffix)).findAny();
        Optional.of(any).filter(Optional::isPresent).orElseThrow(() -> new GeneratorException("后缀不存在"));
        return any.get();
    }
}
