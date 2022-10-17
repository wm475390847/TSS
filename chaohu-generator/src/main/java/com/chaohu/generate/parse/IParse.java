package com.chaohu.generate.parse;

import java.util.List;

/**
 * 自动生成文件服务
 *
 * @author wangmin
 * @date 2022/5/17 13:05
 */
public interface IParse<T> {

    /**
     * 解析为接口信息实体类
     *
     * @return List<T>
     */
    List<T> execute();
}
