package org.sprouts.tss.diff.dto;

import lombok.Data;

import java.util.List;

/**
 * diff结果
 *
 * @author wangmin
 */
@Data
public class CodeDiffResult {

    /**
     * 未识别文件
     */
    private List<String> unidentifiedList;

    /**
     * diff结果
     */
    private List<String> resultStrList;

    /**
     * diff报告
     */
    private String report;

    /**
     * 唯一标识
     */
    private String uuid;

}
