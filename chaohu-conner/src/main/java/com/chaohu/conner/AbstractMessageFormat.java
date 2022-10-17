package com.chaohu.conner;

/**
 * 基础消息格式
 *
 * @author wangmin
 * @date 2022/8/22 15:50
 */
public abstract class AbstractMessageFormat {

    /**
     * 获取格式信息
     *
     * @param info 信息
     * @return 消息格式
     */
    public abstract String getFormat(Object info);
}
