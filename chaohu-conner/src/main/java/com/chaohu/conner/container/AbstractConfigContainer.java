package com.chaohu.conner.container;

import com.chaohu.conner.config.IConfig;
import com.chaohu.conner.exception.ConnerException;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

/**
 * 配置容器基类
 *
 * @author wangmin
 * @date 2022/5/17 16:19
 */
public abstract class AbstractConfigContainer implements IConfigContainer {

    private final Map<String, IConfig> configMap = new HashMap<>(16);

    @Setter
    @Getter
    private Object properties;

    /**
     * 初始化配置容器
     */
    @Override
    public abstract void init();

    @Override
    public void setConfig(IConfig config) {
        addConfig(config);
    }

    @Override
    public IConfig[] getConfigs() {
        List<IConfig> configs = new LinkedList<>(configMap.values());
        return configs.toArray(new IConfig[0]);
    }

    @Override
    public <C extends IConfig> C findConfig(Class<C> config) {
        Optional.ofNullable(config).orElseThrow(() -> new ConnerException("查询的配置类不能为空"));
        Map.Entry<String, IConfig> entry = configMap.entrySet().stream()
                .filter(e -> e.getKey().equals(config.getSimpleName()))
                .findFirst().orElse(null);
        return entry == null ? null : (C) entry.getValue();
    }

    @Override
    public boolean isEmpty() {
        return configMap.isEmpty();
    }

    /**
     * 添加配置类
     * 子类必须调用实现才可以将自定义的配置类添加到容器内部
     *
     * @param config 配置类
     */
    protected void addConfig(IConfig config) {
        configMap.put(config.getClass().getSimpleName(), config);
    }
}
