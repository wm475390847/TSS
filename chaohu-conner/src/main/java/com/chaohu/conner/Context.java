package com.chaohu.conner;

import com.chaohu.conner.config.DingDingConfig;
import com.chaohu.conner.config.HttpConfig;
import com.chaohu.conner.config.IConfig;
import com.chaohu.conner.config.ProductConfig;
import com.chaohu.conner.container.IConfigContainer;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 上下文配置
 *
 * @author wangmin
 * @date 2022/5/17 13:05
 */
public class Context {

    public static Map<Class<?>, Harbor> map = new LinkedHashMap<>(16);

    /**
     * 当前执行的类
     */
    public static Class<?> currentExecuteClass;

    /**
     * 默认的调试模式
     */
    public static String debug;

    /**
     * 是否是调试模式
     */
    public static String isOnDebug;

    /**
     * 详细信息
     */
    public static String detailMessage;

    /**
     * 错误api
     */
    public static String failApi;

    /**
     * 获取配置
     *
     * @param configClass 需要获取的配置类型{@link DingDingConfig}、{@link HttpConfig}、{@link ProductConfig}
     * @return 配置容器
     */
    public static <C extends IConfig> C getConfig(Class<C> configClass) {
        Harbor harbor = getHarbor(currentExecuteClass);
        if (harbor == null) {
            return null;
        }
        IConfigContainer configContainer = harbor.getConfigContainer();
        if (configContainer == null) {
            return null;
        }
        return configContainer.findConfig(configClass);
    }

    /**
     * 获取harbor
     *
     * @param executeClass 正在执行的类{@link #currentExecuteClass}
     * @return Harbor
     */
    public static Harbor getHarbor(Class<?> executeClass) {
        if (map.isEmpty()) {
            return null;
        }
        return map.get(executeClass);
    }
}
