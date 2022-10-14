package com.chaohu.conner.http;

import com.alibaba.fastjson.JSONObject;
import com.chaohu.conner.exception.HttpException;
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
    private static final String SYSTEM_LINE_SEPARATOR = System.getProperty("line.separator");
    private static final String BLANK = "    ";

    /**
     * 获取字符类型的响应体
     *
     * @return 响应体
     */
    public String getStrResult() {
        if (StringUtils.isEmpty(strResult)) {
            try {
                if (response instanceof String) {
                    this.strResult = (String) response;
                } else if (response instanceof Response) {
                    ResponseBody responseBody = ((Response) response).body();
                    Optional.ofNullable(responseBody).orElseThrow(() -> new HttpException("响应体不能为空"));
                    this.strResult = responseBody.string();
                } else {
                    this.strResult = response.toString();
                }
            } catch (IOException e) {
                this.strResult = "日志结果不支持字符串";
            }
        }
        return this.strResult;
    }

    /**
     * 获取映射类型的响应体
     *
     * @return 响应体
     */
    public ResponseInfo getObjResult() {
        String strResult = getStrResult();
        JSONObject object = JSONObject.parseObject(strResult);
        return JSONObject.toJavaObject(object, ResponseInfo.class);
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
        sb.append("Api Request Log:").append(SYSTEM_LINE_SEPARATOR)
                .append(BLANK).append("-headers: ")
                .append(api.getHeaders())
                .append(SYSTEM_LINE_SEPARATOR)
                .append(BLANK).append("-method: ").append(api.getMethodEnum().name()).append(SYSTEM_LINE_SEPARATOR)
                .append(BLANK).append("-url: ").append(api.getUrl()).append(SYSTEM_LINE_SEPARATOR);

        //param
        if (!api.getPartParams().isEmpty()) {
            sb.append(BLANK).append("-partParam: ").append(api.getPartParams()).append(SYSTEM_LINE_SEPARATOR);
        } else if (api.getRequestBody() != null) {
            sb.append(BLANK).append("-body: ").append(api.getRequestBody()).append(SYSTEM_LINE_SEPARATOR);
        } else if (!api.getUrlParams().isEmpty()) {
            sb.append(BLANK).append("-param: ");
            api.getUrlParams().forEach((key, value) -> sb.append(key).append("=").append(value).append("&"));
            sb.replace(sb.length() - 1, sb.length(), "").append(SYSTEM_LINE_SEPARATOR);
        }

        //response
        sb.append(BLANK).append("-response: ").append(getStrResult()).append(SYSTEM_LINE_SEPARATOR)
                .append(BLANK).append("-response time: ").append(getResponseTime()).append("ms");
        return sb.toString();
    }

    private String strResult;
    private long startTime;
    private long endTime;
    private T response;
    private Api api;
}
