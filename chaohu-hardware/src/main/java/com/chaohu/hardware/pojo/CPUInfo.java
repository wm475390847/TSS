package com.chaohu.hardware.pojo;

import lombok.Data;

/**
 * cpu信息
 *
 * @author wangmin
 */
@Data
public class CPUInfo {

    /**
     * cpu占用率
     */
    private double cpuRatio;

    @Override
    public String toString() {
        return "cpuRatio=" + cpuRatio;
    }
}
