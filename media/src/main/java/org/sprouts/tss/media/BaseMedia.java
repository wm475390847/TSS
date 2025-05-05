package org.sprouts.tss.media;

/**
 * 抽象媒体处理器
 *
 * @author wangmin
 * @date 2023/7/18 17:43
 */
public abstract class BaseMedia<T> implements Media<T> {


    public T compare() {
        return null;
    }

    public T cut() {
        return null;
    }
}
