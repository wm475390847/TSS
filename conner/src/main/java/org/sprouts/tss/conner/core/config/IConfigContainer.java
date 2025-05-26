package org.sprouts.tss.conner.core.config;

import java.util.Properties;

/**
 * 配置容器接口
 * <P>用来存放所有的自定义配置
 *
 * @author wangmin
 * @date 2022/5/17 16:05
 */
public interface IConfigContainer {
    /**
     * 初始化容器
     */
    void init();

    /**
     * 向配置容器内添加属性
     * <P>属性的作用：读取resources文件夹内的数据，将其传入容器内部供配置类使用
     *
     * @param properties 属性
     */
    void setProperties(Properties properties);

    /**
     * 获取属性配置
     *
     * @return 属性配置
     */
    Properties getProperties();

    /**
     * 设置容器内的配置类
     * <P>设置新的配置类后会重新初始化配置容器
     *
     * @param config 配置类
     */
    void setConfig(IConfig config);

    /**
     * 查询指定配置类
     *
     * @param config new Config()
     * @param <C>    IConfig的子类
     * @return 配置类
     */
    <C extends IConfig> C getConfig(Class<C> config);

    /**
     * 获取配置容器内所有的配置类
     *
     * @return 配置类数组
     */
    IConfig[] getConfigs();

    /**
     * 判断配置容器是否为空
     *
     * @return 容器内部为空时返回true，不为空时返回false
     */
    boolean isEmpty();
}
