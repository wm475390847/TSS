package org.sprouts.tss.conner.config;

/**
 * 抽象配置类
 * <P>暂时没有什么逻辑
 *
 * @author wangmin
 * @date 2022/5/17 13:05
 */
public abstract class AbstractConfig implements IConfig, Cloneable {

    @Override
    public AbstractConfig clone() {
        try {
            return (AbstractConfig) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
