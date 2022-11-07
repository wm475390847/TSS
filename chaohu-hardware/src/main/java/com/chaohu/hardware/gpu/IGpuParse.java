package com.chaohu.hardware.gpu;

import com.chaohu.hardware.pojo.GpuInfo;

import java.util.List;
import java.util.Optional;

/**
 * @author wangmin
 * @date 2022/4/14 09:24
 */
public interface IGpuParse {

    /**
     * 获取gpu信息
     *
     * @return Optional<List < GpuInfo>>
     */
    Optional<List<GpuInfo>> getGpuInfo();

    /**
     * 设置命令
     *
     * @param command 命令
     */
    void setCommand(String command);
}
