package org.sprouts.tss.diff.core;

/**
 * 源码diff
 *
 * @author wangmin
 */
public interface ICodeDiff<T> extends IDiff {

    /**
     * diff
     */
    void diff();

    /**
     * 清除工作目录
     */
    void clear();

    /**
     * 获取结果
     *
     * @return 结果
     */
    T getResult();
}
