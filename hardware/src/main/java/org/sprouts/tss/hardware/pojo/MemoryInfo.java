package org.sprouts.tss.hardware.pojo;

import lombok.Data;

/**
 * 内存信息
 *
 * @author wangmin
 */
@Data
public class MemoryInfo {

    /**
     * 内存使用量
     */
    private long memoryUse;

    /**
     * 内存总量
     */
    private long memoryTotal;

    /**
     * 内存占用率
     */
    private double memoryRatio;

    @Override
    public String toString() {
        return "memoryRatio=" + memoryRatio;
    }
}
