package org.sprouts.tss.hardware.gpu;

import org.sprouts.tss.hardware.enums.GpuTypeEnum;
import org.sprouts.tss.hardware.pojo.GpuInfo;
import lombok.Setter;

import java.util.List;
import java.util.Optional;

/**
 * @author wangmin
 * @date 2022/4/14 09:14
 */
@Setter
public abstract class BaseGpuParse implements IGpuParse {

    private String command;

    private String getCommand() {
        return command == null ? GpuTypeEnum.findCommandByGpuParse(this) : command;
    }

    @Override
    public Optional<List<GpuInfo>> getGpuInfo() {
        return find(getCommand());
    }

    /**
     * 查询gpu信息
     *
     * @param command 命令
     * @return Optional<List < GpuInfo>>
     */
    protected abstract Optional<List<GpuInfo>> find(String command);
}
