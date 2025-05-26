package org.sprouts.tss.conner.http.response;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import org.sprouts.tss.conner.exception.ConnerException;
import org.sprouts.tss.conner.http.client.Api;
import lombok.Data;
import lombok.experimental.Accessors;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Optional;

/**
 * 结果日志
 *
 * @author wangmin
 * @date 2022/5/17 13:05
 */
@Data
@Accessors(chain = true)
public class ResponseLog<T> {
    private static final String SYSTEM_LINE_SEPARATOR = System.lineSeparator();
    private static final String BLANK = "    ";

    private String strResult;
    private long startTime;
    private long endTime;
    private T response;
    private Api api;

    /**
     * 获取字符类型的响应体
     *
     * @return 响应体
     */
    public String getStrResult() {
        if (StringUtils.isNotEmpty(strResult)) {
            return strResult;
        }

        try {
            if (response instanceof String) {
                this.strResult = (String) response;
            } else if (response instanceof Response) {
                this.strResult = handleResponse((Response) response);
            } else {
                this.strResult = response.toString();
            }
        } catch (IOException e) {
            this.strResult = "日志结果不支持字符串: " + e.getMessage();
        }
        return this.strResult;
    }

    /**
     * 处理 Response 类型的响应体
     *
     * @param httpResponse okhttp 的 Response 对象
     * @return 字符串形式的响应体
     * @throws IOException 如果读取失败
     */
    private String handleResponse(Response httpResponse) throws IOException {
        try (httpResponse) {
            ResponseBody responseBody = Optional.ofNullable(httpResponse.body())
                    .orElseThrow(() -> new ConnerException("响应体不能为空"));

            return responseBody.string();
        }
    }

    /**
     * 获取请求id
     *
     * @return 请求id
     */
    public String getRequestId() {
        return ((Response) getResponse()).header("RequestId");
    }

    /**
     * 获取映射类型的响应体
     *
     * @return 响应体
     */
    public ResponseInfo responseInfo() {
        String strResult = getStrResult();
        JSONObject object = JSONObject.parseObject(strResult);
        return JSONObject.toJavaObject(object, ResponseInfo.class);
    }

    /**
     * 获取指定类型的响应体
     *
     * @param clazz 指定类型
     * @return 响应体
     */
    public T responseInfo(Class<T> clazz) {
        String strResult = getStrResult();
        JSONObject object = JSONObject.parseObject(strResult);
        return JSONObject.toJavaObject(object, clazz);
    }

    /**
     * jsonpath获取数据
     *
     * @param path   path
     * @param tClass 结果类型
     * @return 相应体
     */
    public T JSONPath(String path, Class<T> tClass) {
        return JSONPath.read(getStrResult(), path, tClass);
    }

    /**
     * 获取响应时间
     *
     * @return 时间
     */
    public long getResponseTime() {
        return endTime - startTime;
    }

    @Override
    public String toString() {
        return buildLog();
    }

    /**
     * 构建日志格式
     *
     * @return 日志
     */
    private String buildLog() {
        StringBuilder sb = new StringBuilder();
        sb.append("Api Request Log:").append(SYSTEM_LINE_SEPARATOR).append(BLANK).append("-headers: ").append(api.getHeaders()).append(SYSTEM_LINE_SEPARATOR).append(BLANK).append("-method: ").append(api.getMethodEnum().name()).append(SYSTEM_LINE_SEPARATOR).append(BLANK).append("-url: ").append(api.getUrl()).append(SYSTEM_LINE_SEPARATOR);

        //param
        if (!api.getFormDataParams().isEmpty()) {
            sb.append(BLANK).append("-partParam: ").append(api.getFormDataParams()).append(SYSTEM_LINE_SEPARATOR);
        } else if (api.getRequestBody() != null) {
            sb.append(BLANK).append("-body: ").append(api.getRequestBody()).append(SYSTEM_LINE_SEPARATOR);
        } else if (!api.getUrlParams().isEmpty()) {
            sb.append(BLANK).append("-param: ");
            api.getUrlParams().forEach((key, value) -> sb.append(key).append("=").append(value).append("&"));
            sb.replace(sb.length() - 1, sb.length(), "").append(SYSTEM_LINE_SEPARATOR);
        }

        //response
        sb.append(BLANK).append("-response: ").append(getStrResult()).append(SYSTEM_LINE_SEPARATOR).append(BLANK).append("-response time: ").append(getResponseTime()).append("ms");
        return sb.toString();
    }
}
