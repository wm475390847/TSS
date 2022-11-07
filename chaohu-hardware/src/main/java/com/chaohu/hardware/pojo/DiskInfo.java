package com.chaohu.hardware.pojo;

import lombok.Data;

/**
 * 磁盘信息
 *
 * @author wangmin
 */
@Data
public class DiskInfo {

    /**
     * 磁盘使用量
     */
    private long diskUse;

    /**
     * 磁盘剩余量
     */
    private long diskResidue;

    /**
     * 磁盘总量
     */
    private long diskTotal;

    private double diskRatio;

    @Override
    public String toString() {
        return "diskRatio=" + diskRatio;
    }
}
