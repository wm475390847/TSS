package com.chaohu.conner.http;

import com.alibaba.fastjson.JSONObject;
import com.chaohu.conner.config.HttpConfig;
import com.chaohu.conner.http.connector.IConnector;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;

/**
 * 调用http请求的基类
 *
 * @author wangmin
 * @date 2022/5/17 13:05
 */
@Slf4j
public abstract class AbstractHttpRequest implements IRequest<Response> {

    /**
     * 当前请求体
     */
    private Object currentBody;

    /**
     * http请求的配置
     */
    private HttpConfig httpConfig;

    @Override
    public ResponseLog<Response> execute() {
        Api api = buildApi();
        IConnector<Response> connector = getConnector(api);
        connector.api(api).config(httpConfig).execute();
        return connector.getLog();
    }

    @Override
    public Api getApi() {
        Api api = buildApi();
        return getConnector(api).getApi();
    }

    @Override
    public IRequest<Response> addHttpConfig(HttpConfig httpConfig) {
        this.httpConfig = httpConfig;
        return this;
    }

    @Override
    public IRequest<Response> addRequestBody(Object body) {
        this.currentBody = body;
        if (body instanceof JSONObject) {
            JSONObject currentBody = (JSONObject) getCurrentBody();
            currentBody.putAll((JSONObject) body);
            this.currentBody = currentBody;
        }
        return this;
    }

    @Override
    public <Y> IRequest<Response> modifyRequestBody(String key, Y value) {
        JSONObject currentBody = (JSONObject) getCurrentBody();
        currentBody.put(key, value);
        this.currentBody = currentBody;
        return this;
    }

    @Override
    public IRequest<Response> addSign(String ak, String sk) {
        this.httpConfig.sign(ak, sk);
        return null;
    }

    @Override
    public IRequest<Response> page(Integer pageIndex) {
        setPage(pageIndex);
        return this;
    }

    @Override
    public IRequest<Response> size(Integer pageSize) {
        setSize(pageSize);
        return this;
    }

    /**
     * 设置页码
     * <P>有需要的子类实现
     *
     * @param page 页码
     */
    protected void setPage(Integer page) {

    }

    /**
     * 设置页大小
     * <P>有需要的子类实现
     *
     * @param size 大小
     */
    protected void setSize(Integer size) {
    }

    /**
     * 构建api类，api类中包含了调用http请求的参数
     * <P>POST: new Api.Builder().path("").method({@link MethodEnum}.POST).contentType("application/json").bodyContent({@link #getCurrentBody()}).build();
     * <p>GET: new Api.Builder().path("").method({@link MethodEnum}.GET).contentType("application/json").urlParam("a", a).urlParam("b", b).build();
     *
     * @return Api类
     */
    protected abstract Api buildApi();

    /**
     * 构建请求体，body中包含请求所需要的参数
     * <p>Object is null: return null; Object is JSONObject: return JSONObject; Object is Array: return Array
     *
     * @return 请求体
     */
    protected abstract Object buildBody();

    /**
     * 获取当前请求体
     * <P>当使用自定义的请求体时，更新类中包含的变量{@link #currentBody}，此变量的优先级高于子类构建的请求体{@link #buildBody()}
     *
     * @return 当前请求体
     */
    protected Object getCurrentBody() {
        return currentBody == null ? buildBody() : currentBody;
    }

    private IConnector<Response> getConnector(Api api) {
        return api.getMethodEnum().getConnector();
    }
}
