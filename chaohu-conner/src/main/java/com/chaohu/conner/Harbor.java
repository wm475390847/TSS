package com.chaohu.conner;

import com.chaohu.conner.container.IConfigContainer;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author wangmin
 * @date 2022/8/25 14:06
 */
@Data
@Accessors(chain = true)
public class Harbor {

    /**
     * 配置容器
     */
    IConfigContainer configContainer;

    /**
     * 收集器
     */
    AbstractCollector abstractCollector;

    /**
     * 是否发送通知
     */
    Boolean sendInfo;

    /**
     * 是否保存用例数据
     */
    Boolean saveInfo;
}
