package org.sprouts.tss.hardware.gpu;

import org.sprouts.tss.hardware.pojo.GpuInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

/**
 * @author wangmin
 * @date 2022/4/14 09:15
 */
@Slf4j
public class IntelGpuParse extends BaseGpuParse {

    @Override
    protected Optional<List<GpuInfo>> find(String command) {
        return Optional.empty();
    }
}
