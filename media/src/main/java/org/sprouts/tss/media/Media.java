package org.sprouts.tss.media;

/**
 * 媒体处理器
 *
 * @author wangmin
 * @date 2023/7/18 17:38
 */
public interface Media<T> {

    /**
     * 对比
     *
     * @return 对比结果
     */
    T compare();

    /**
     * 裁切
     *
     * @return 裁切结果
     */
    T cut();
}
