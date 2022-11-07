package com.chaohu.hardware.pojo;

import lombok.Data;

import java.util.List;

/**
 * @author wangmin
 */
@Data
public class GpuInfo {
    /**
     * 名称
     */
    private String name;
    /**
     * 总内存
     */
    private String totalMemory;
    /**
     * 已使用内存
     */
    private String usedMemory;
    /**
     * 空闲内存
     */
    private String freeMemory;

    /**
     * 使用率 整形，最大为100
     */
    private int memoryRatio;

    /**
     * gpu的使用率 string
     */
    private Integer gpuRatio;

    /**
     * 进程信息线
     */
    private List<ProcessInfo> processInfos;

    @Override
    public String toString() {
        return "name='" + name + '\'' +
                ", memoryRatio=" + memoryRatio +
                ", gpuRatio=" + gpuRatio;
    }
}