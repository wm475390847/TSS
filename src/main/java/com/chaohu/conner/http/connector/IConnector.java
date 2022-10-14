package com.chaohu.conner.http.connector;

import com.chaohu.conner.config.HttpConfig;
import com.chaohu.conner.http.Api;
import com.chaohu.conner.http.ResponseLog;
import okhttp3.Response;

/**
 * @author wangmin
 * @date 2022/10/13 10:31
 */
public interface IConnector<T> {

    /**
     * 执行
     *
     * @return 请求响应
     */
    Response execute();

    /**
     * 放入api对象
     *
     * @param api api对象
     * @return IConnector<T>
     */
    IConnector<T> api(Api api);

    /**
     * 获取api
     *
     * @return Api对象
     */
    Api getApi();

    /**
     * 放入http的配置
     *
     * @param httpConfig http配置
     * @return IConnector<T>
     */
    IConnector<T> config(HttpConfig httpConfig);

    /**
     * 获取日志
     *
     * @return 请求日志
     */
    ResponseLog<T> getLog();
}
