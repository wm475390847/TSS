package com.chaohu.conner.http;

import com.chaohu.conner.config.HttpConfig;
import com.chaohu.conner.exception.HttpException;
import com.chaohu.conner.http.connector.IConnector;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Api属性类
 * <P>包含实现接口调用所需要的属性
 *
 * @author wangmin
 * @date 2022/5/17 13:05
 */
@Getter
@Slf4j
public class Api {

    private static final String SOLIDUS = "/";
    private final Map<String, String> partParams = new HashMap<>();
    private final Map<String, String> urlParams = new HashMap<>();
    private final Map<String, String> partFiles = new HashMap<>();
    private final Map<String, String> headers = new HashMap<>();
    private final Map<String, String> sign = new HashMap<>();
    private final String contentType;
    private final MethodEnum methodEnum;
    private final Boolean ignoreSsl;
    private final Object requestBody;
    private final String url;
    private String hostname;
    private String ipaddress;
    private String baseUrl;
    private Integer port;
    private String path;
    private Boolean pure = false;

    public Api(Builder builder) {
        this.partParams.putAll(builder.formDataParts);
        this.urlParams.putAll(builder.urlParamParts);
        this.partFiles.putAll(builder.fileParts);
        this.headers.putAll(builder.headers);
        this.sign.putAll(builder.sign);
        this.contentType = builder.contentType;
        this.requestBody = builder.requestBody;
        this.methodEnum = builder.methodEnum;
        this.ignoreSsl = builder.ignoreSsl;
        this.baseUrl = builder.baseUrl;
        this.url = builder.url;
        this.hostname = builder.hostname;
        this.ipaddress = builder.ipaddress;
        this.path = builder.path;
        this.port = builder.port;
    }

    /**
     * 执行
     *
     * @return 响应日志
     */
    public ResponseLog<Response> execute() {
        IConnector<Response> connector = this.methodEnum.getConnector();
        connector.api(this).execute();
        return connector.getLog();
    }

    /**
     * 纯净模式
     * 使用纯净模式后接口调用不受全局配置影响
     *
     * @return this
     */
    public Api pure() {
        this.pure = true;
        return this;
    }

    /**
     * 将通http配置更新到api类中，如果没有就不进行更新
     *
     * @param config http配置类
     */
    public void setHttpConfig(HttpConfig config) {
        // 如果是纯净模式，就不需要将全局配置放进去
        if (pure) {
            return;
        }
        if (config != null) {
            hostname = config.getHostname() != null && StringUtils.isEmpty(hostname) ? config.getHostname() : hostname;
            // 如果config中host不为空并且api中host为空则使用config中的host，否则使用api中的host
            ipaddress = config.getIpaddress() != null && StringUtils.isEmpty(ipaddress) ? config.getIpaddress() : ipaddress;
            // 如果config中基础url不为空并且api中基础url为空则使用config中的基础url，否则使用api中的基础url
            baseUrl = config.getBaseUrl() != null && StringUtils.isEmpty(baseUrl) ? config.getBaseUrl() : baseUrl;
            // 如果config中port不为空并且api中port为空则使用config中的port，否则使用api中的port
            port = config.getPort() != null && port == null ? config.getPort() : port;
            // 如果config中的加签参数不为空并且api中允许加签则进行加签处理，否则不加签
            if (!config.getSign().isEmpty()) {
                sign.putAll(config.getSign());
            }
            // 如果http配置的头部配置类不为空就更新
            if (!config.getRequestHeaders().isEmpty()) {
                headers.putAll(config.getRequestHeaders());
            }
        }
    }

    public String getHostname() {
        return new Request.Builder().url(getUrl()).build().url().host();
    }

    /**
     * 获取完成的url
     *
     * @return 完整的url
     */
    public String getUrl() {
        String fullUrl = createFullUrl();
        // 加签
        if (!sign.isEmpty()) {
            String signUrl = addSign(sign, urlParams);
            fullUrl = fullUrl.contains("?") ? fullUrl.split("\\?")[0] : url;
            return fullUrl + "?" + signUrl;
        }
        return fullUrl;
    }

    /**
     * 创建完整的url<br>如果只有baseUrl则需要http://${host}+/处理+${path}+?+${param}</P>
     *
     * @return 完整的url
     */
    private String createFullUrl() {
        String str;
        if (url != null) {
            str = url;
        } else if (baseUrl != null && path != null) {
            StringBuilder sb = new StringBuilder(baseUrl);
            if (!baseUrl.endsWith(SOLIDUS)) {
                sb.append(SOLIDUS);
            }
            path = path.startsWith(SOLIDUS) ? path.replaceFirst(SOLIDUS, "") : path;
            sb.append(path);
            str = sb.toString();
        } else {
            throw new HttpException("URL为空");
        }

        if (urlParams.isEmpty()) {
            return str;
        } else {
            StringBuilder pathSb = new StringBuilder();
            urlParams.forEach((key, value) -> pathSb.append(key).append("=").append(value).append("&"));
            if (pathSb.toString().endsWith("&")) {
                pathSb.replace(pathSb.length() - 1, pathSb.length(), "");
            }
            return str + "?" + pathSb;
        }
    }

    /**
     * 加签
     *
     * @param sign 鉴权内容
     * @param map  get的请求参数
     * @return 加签后的url
     */
    private String addSign(Map<String, String> sign, Map<String, String> map) {
        //TODO 每个公司加签方式不一样，需要自定义
        return null;
    }

    /**
     * http参数构建类
     */
    public static class Builder {
        private final Map<String, String> formDataParts = new HashMap<>();
        private final Map<String, String> urlParamParts = new HashMap<>();
        private final Map<String, String> fileParts = new HashMap<>();
        private final Map<String, String> headers = new HashMap<>();
        private final Map<String, String> sign = new HashMap<>();
        private MethodEnum methodEnum;
        private Object requestBody;
        private Boolean ignoreSsl = true;
        private String contentType = "application/json";
        private String path;
        private String url;
        private String baseUrl;
        private String hostname;
        private String ipaddress;
        private Integer port;

        public Builder headers(Map<String, String> headers) {
            this.headers.putAll(headers);
            return this;
        }

        public Builder header(String key, String value) {
            this.headers.put(key, value);
            return this;
        }

        public Builder sign(String ak, String sk) {
            this.sign.put(ak, sk);
            return this;
        }

        public Builder formDataParts(Map<String, String> formDataParts) {
            this.formDataParts.putAll(formDataParts);
            return this;
        }

        public Builder formDataPart(String key, String value) {
            this.formDataParts.put(key, value);
            return this;
        }

        public Builder filePart(String key, String filePath) {
            this.fileParts.put(key, filePath);
            return this;
        }

        public Builder fileParts(Map<String, String> fileParts) {
            this.fileParts.putAll(fileParts);
            return this;
        }

        public Builder urlParamPart(String key, Object object) {
            if (object != null) {
                this.urlParamParts.put(key, String.valueOf(object));
            }
            return this;
        }

        public Builder requestBody(Object requestBody) {
            this.requestBody = requestBody;
            return this;
        }

        public Builder contentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public Builder ignoreSsl(boolean ignoreSsl) {
            this.ignoreSsl = ignoreSsl;
            return this;
        }

        @Deprecated
        public Builder method(String method) {
            this.methodEnum = MethodEnum.findEnumByType(method);
            return this;
        }

        public Builder method(MethodEnum methodEnum) {
            this.methodEnum = methodEnum;
            return this;
        }

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        public Builder hostname(String hostname) {
            this.hostname = hostname;
            return this;
        }

        public Builder ipaddress(String ipaddress) {
            this.ipaddress = ipaddress;
            return this;
        }

        public Builder port(Integer port) {
            this.port = port;
            return this;
        }

        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Api build() {
            return new Api(this);
        }
    }
}
