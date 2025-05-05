package org.sprouts.tss.generator.enums;

import org.sprouts.tss.generator.execption.GeneratorException;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author wangmin
 * @date 2022/5/17 13:05
 */
@Getter
public enum KeywordEnum {
    /**
     * 包名关键字package
     */
    PACKAGE("package", "Package"),

    IMPORT("import", "Import"),

    PUBLIC("public", "Public"),
    ;

    KeywordEnum(String keyword, String transfer) {
        this.keyword = keyword;
        this.transfer = transfer;
    }

    public final String keyword;
    public final String transfer;

    public static KeywordEnum findByKeyword(String keyword) {
        Optional.ofNullable(keyword).orElseThrow(() -> new GeneratorException("keyword 不能为空"));
        Optional<KeywordEnum> any = Arrays.stream(values()).filter(e -> e.getKeyword().equals(keyword)).findAny();
        Optional.of(any).filter(Optional::isPresent).orElseThrow(() -> new GeneratorException("关键字不存在"));
        return any.get();
    }

    /**
     * 转换关键词
     *
     * @param str str
     * @return string
     */
    public static String transferKeyword(String str) {
        return Arrays.stream(KeywordEnum.values()).filter(e -> str.equals(e.getKeyword()))
                .map(e -> str.replace(e.getKeyword(), e.getTransfer())).findFirst().orElse(str);
    }
}
