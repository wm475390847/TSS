package com.chaohu.conner.http;

import com.chaohu.conner.exception.HttpException;
import com.chaohu.conner.config.HttpConfig;
import com.shuwen.openapi.gateway.util.SignHelperV2;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.Request;
import org.apache.commons.lang3.StringUtils;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
    private final String path;
    private final String url;
    private String innerHost;
    private String baseUrl;
    private Integer port;

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
        this.innerHost = builder.innerHost;
        this.path = builder.path;
        this.port = builder.port;
    }

    /**
     * 将通http配置更新到api类中，如果没有就不进行更新
     *
     * @param config http配置类
     */
    public void setHttpConfig(HttpConfig config) {
        if (config == null) {
            return;
        }
        // 如果config中host不为空并且api中host为空则使用config中的host，否则使用api中的host
        innerHost = config.getHost() != null && StringUtils.isEmpty(innerHost) ? config.getHost() : innerHost;
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

    /**
     * 获取完成的url
     *
     * @return 完整的url
     */
    public String getUrl() {
        String fullUrl = url == null ? createFullUrlByBaseUrl() : createFullUrlByUrl();
        // 加签
        if (!sign.isEmpty()) {
            String signUrl = addSign(sign, urlParams);
            fullUrl = fullUrl.contains("?") ? fullUrl.split("\\?")[0] : url;
            return fullUrl + "?" + signUrl;
        }
        return fullUrl;
    }

    /**
     * 转换为okhttp3.{@link Request}对象
     *
     * @return Request
     */
    public Request toRequest() {
        return new Request.Builder().url(url).build();
    }

    /**
     * 判断请求utl是否为https的请求
     *
     * @return 是为true，不是为false
     */
    public boolean isHttps() {
        return getUrl().contains("https://");
    }

    /**
     * 获取代理
     *
     * @return 代理类
     */
    public Proxy getProxy() {
        if (innerHost == null) {
            return null;
        }
        // 得到协议、host、端口
        HttpUrl httpUrl = toRequest().url();
        String scheme = httpUrl.scheme();
        String hostName = httpUrl.host();
        port = port == null ? scheme.contains("http") ? 80 : 443 : port;
        // 绑定url
        InetAddress byAddress = null;
        try {
            byAddress = InetAddress.getByAddress(hostName, ipParse(innerHost));
        } catch (UnknownHostException e) {
            log.error("host绑定错误: {}", e.getMessage());
        }
        InetSocketAddress inetSocketAddress = new InetSocketAddress(byAddress, port);
        return new Proxy(Proxy.Type.HTTP, inetSocketAddress);
    }

    /**
     * ip解析
     *
     * @param host host 127.0.0.1
     * @return byte[]
     */
    private byte[] ipParse(String host) {
        String[] ipStr = host.split("\\.");
        byte[] ipBuf = new byte[4];
        for (int i = 0; i < ipBuf.length; i++) {
            ipBuf[i] = (byte) (Integer.parseInt(ipStr[i]) & 0xff);
        }
        return ipBuf;
    }

    /**
     * 创建完整的url<br>如果url是完整的则需要http://${host}/${path}+?+${param}
     *
     * @return 完整的url
     */
    private String createFullUrlByUrl() {
        Optional.ofNullable(url).orElseThrow(() -> new HttpException("url为空"));
        StringBuilder sb = new StringBuilder(url);
        return createFullUrl(sb);
    }

    /**
     * 创建完整的url<br>如果只有baseUrl则需要http://${host}+/处理+${path}+?+${param}</P>
     *
     * @return 完整的url
     */
    private String createFullUrlByBaseUrl() {
        Optional.ofNullable(baseUrl).orElseThrow(() -> new HttpException("baseUrl为空"));
        StringBuilder sb = new StringBuilder(baseUrl);
        String newPath = path;
        if (newPath != null) {
            if (!baseUrl.endsWith(SOLIDUS)) {
                sb.append(SOLIDUS);
            }
            newPath = newPath.startsWith(SOLIDUS) ? newPath.replaceFirst(SOLIDUS, "") : newPath;
            sb.append(newPath);
        }
        return createFullUrl(sb);
    }

    /**
     * 创建完整的url<br>如果是get请求组合成：http://xxx.xxx.xx/xxx/xxx?xxx=11&sss=111
     *
     * @param sb url的StringBuilder
     * @return 带有参数的完整url
     */
    private String createFullUrl(StringBuilder sb) {
        if (urlParams.isEmpty()) {
            return sb.toString();
        } else {
            StringBuilder pathSb = new StringBuilder();
            urlParams.forEach((key, value) -> pathSb.append(key).append("=").append(value).append("&"));
            if (pathSb.toString().endsWith("&")) {
                pathSb.replace(pathSb.length() - 1, pathSb.length(), "");
            }
            return sb + "?" + pathSb;
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
        String signUrl = null;
        for (Map.Entry<String, String> entry : sign.entrySet()) {
            signUrl = SignHelperV2.getSignUrl(entry.getKey(), entry.getValue(), map);
        }
        return signUrl;
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
        private String innerHost;
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

        public Builder innerHost(String innerHost) {
            this.innerHost = innerHost;
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
