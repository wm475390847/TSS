package com.chaohu.conner.http;

import com.chaohu.conner.config.HttpConfig;

/**
 * 请求接口
 * 执行各种类型的http请求调用
 *
 * @author wangmin
 * @date 2022/5/17 13:05
 */
public interface IRequest<T> {

    /**
     * 执行api的调用
     *
     * @return 相应日志
     */
    ResponseLog<T> execute();

    /**
     * 自定义请求体
     * <P>更新当前的请求体
     *
     * @param requestBody 请求体
     * @return IApi<T>
     */
    IRequest<T> addRequestBody(Object requestBody);

    /**
     * 修改请求体
     *
     * @param key   键
     * @param value 值
     * @return IApi<T>
     */
    <Y> IRequest<T> modifyRequestBody(String key, Y value);

    /**
     * 添加一个http配置
     *
     * @param httpConfig http配置
     * @return IApi<T>
     */
    IRequest<T> addHttpConfig(HttpConfig httpConfig);

    /**
     * 加签
     *
     * @param ak ak
     * @param sk sk
     * @return IApi<T>
     */
    IRequest<T> addSign(String ak, String sk);

    /**
     * 设置页数量
     *
     * @param page 页数量
     * @return IApi<T>
     */
    IRequest<T> page(Integer page);

    /**
     * 设置页大小
     *
     * @param size 页大小
     * @return IApi<T>
     */
    IRequest<T> size(Integer size);
}
