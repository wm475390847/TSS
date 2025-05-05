package org.sprouts.tss.conner.enums;

import lombok.Getter;

/**
 * 步骤枚举
 */
@Getter
public enum CaseStepEnum {

    START("开始"),
    SUCCESS("成功"),
    SKIPPED("跳过"),
    FAILED("失败"),
    ;


    CaseStepEnum(String name) {
        this.name = name;
    }

    private final String name;
}
