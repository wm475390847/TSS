package com.chaohu.generate.core;

import com.chaohu.generate.pojo.Structure;

import java.util.Map;

/**
 * 生成器接口
 *
 * @author wangmin
 * @date 2022/5/17 13:05
 */
public interface IGenerator {

    /**
     * 执行
     */
    void execute();

    /**
     * 获取需要生成的文件数量
     *
     * @return 文件数量
     */
    Integer count();

    /**
     * 获取需要生成的文件结构
     *
     * @return 数据结构
     */
    Map<Integer, Structure> getStructureMap();

    /**
     * 加载
     *
     * @return IGenerator
     */
    IGenerator load();
}
