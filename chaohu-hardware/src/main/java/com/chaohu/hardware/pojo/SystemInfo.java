package com.chaohu.hardware.pojo;

import lombok.Data;

import java.util.List;
import java.util.Optional;

/**
 * @author wangmin
 * @date 2022/4/14 11:55
 */
@Data
public class SystemInfo {

    private CPUInfo cpuInfo;

    private MemoryInfo memoryInfo;

    private Optional<List<GpuInfo>> gpuInfo;

    private DiskInfo diskInfo;
}
