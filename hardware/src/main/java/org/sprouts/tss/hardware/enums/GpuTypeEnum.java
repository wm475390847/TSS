package org.sprouts.tss.hardware.enums;

import org.sprouts.tss.hardware.gpu.IGpuParse;
import org.sprouts.tss.hardware.gpu.IntelGpuParse;
import org.sprouts.tss.hardware.gpu.NvidiaGpuParse;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author wangmin
 * @date 2022/4/14 09:10
 */
public enum GpuTypeEnum {

    /**
     * 英特尔
     */
    INTEL("Intel", "", new IntelGpuParse()),

    /**
     * 英伟达
     */
    NVIDIA("Nvidia", "nvidia-smi -q -x", new NvidiaGpuParse()),

    ;

    GpuTypeEnum(String name, String command, IGpuParse gpuParse) {
        this.name = name;
        this.command = command;
        this.gpuParse = gpuParse;
    }

    @Getter
    private final String name;

    @Getter
    private final String command;

    @Getter
    private final IGpuParse gpuParse;

    public static GpuTypeEnum findByGpuType(String typeName) {
        Optional.ofNullable(typeName).orElseThrow(() -> new RuntimeException("gpu类型不存在"));
        Optional<GpuTypeEnum> any = Arrays.stream(values()).filter(e -> e.getName().equals(typeName)).findAny();
        return any.orElse(null);
    }

    public static String findCommandByGpuParse(IGpuParse gpuParse) {
        Optional.ofNullable(gpuParse).orElseThrow(() -> new RuntimeException("gpu解析类不存在"));
        Optional<GpuTypeEnum> any = Arrays.stream(values()).filter(e -> e.getGpuParse().equals(gpuParse)).findAny();
        Optional.of(any).filter(Optional::isPresent).orElseThrow(() -> new RuntimeException("gpu解析类不存在"));
        return any.get().getCommand();
    }
}
