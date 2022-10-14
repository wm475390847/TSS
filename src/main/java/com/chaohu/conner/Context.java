package com.chaohu.conner;

import com.chaohu.conner.config.HttpConfig;
import com.chaohu.conner.config.IConfig;
import com.chaohu.conner.container.IConfigContainer;

import java.util.HashMap;
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
     * 获取http的配置
     *
     * @return http配置
     */
    public static HttpConfig getHttpConfig() {
        if (map.isEmpty()) {
            return null;
        }
        Harbor harbor = getHarbor(Context.currentExecuteClass);
        if (harbor == null) {
            return null;
        }
        IConfigContainer configContainer = harbor.getConfigContainer();
        if (configContainer == null) {
            return null;
        }
        return configContainer.findConfig(HttpConfig.class);
    }

    /**
     * 获取配置容器
     *
     * @return 配置容器
     */
    public static <C extends IConfig> C getContainer(Class<C> configClass) {
        Harbor harbor = getHarbor(configClass);
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
     * @param clazz 类
     * @return Harbor
     */
    public static Harbor getHarbor(Class<?> clazz) {
        if (map.isEmpty()) {
            return null;
        }
        return map.get(clazz);
    }
}
