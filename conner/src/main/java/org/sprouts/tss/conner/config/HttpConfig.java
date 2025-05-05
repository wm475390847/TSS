package org.sprouts.tss.conner.config;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

/**
 * http相关的配置
 * <P>包含请求头部配置、url的配置、加签配置
 *
 * @author wangmin
 * @date 2022/5/17 13:05
 */
@Getter
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class HttpConfig extends AbstractConfig {

    private String baseUrl;
    private String hostname;
    private String ipaddress;
    private Integer port;
    private final Map<String, String> sign = new HashMap<>();
    private final Map<String, String> requestHeaders = new HashMap<>();

    /**
     * 设置请求头中的referer
     *
     * @param referer referer
     * @return HeadersConfig
     */
    public HttpConfig referer(String referer) {
        requestHeaders.put("Referer", referer);
        return this;
    }

    /**
     * 设置请求头中的token
     *
     * @param token token
     * @return HeadersConfig
     */
    public HttpConfig token(String token) {
        requestHeaders.put("token", token);
        return this;
    }

    /**
     * 设置请求头中的cookie
     *
     * @param cookie cookie
     * @return HeadersConfig
     */
    public HttpConfig cookie(String cookie) {
        requestHeaders.put("Cookie", cookie);
        return this;
    }

    /**
     * 设置请求头中的secretKey
     *
     * @param secretKey 一串加密字符串
     * @return HeadersConfig
     */
    public HttpConfig secretKey(String secretKey) {
        requestHeaders.put("SecretKey", secretKey);
        return this;
    }

    /**
     * 设置请求头中其他的属性
     *
     * @param key   键
     * @param value 值
     * @return HeadersConfig
     */
    public HttpConfig other(String key, String value) {
        requestHeaders.put(key, value);
        return this;
    }

    /**
     * 设置基础的url
     *
     * @param baseUrl host的名字
     * @return HeadersConfig
     */
    public HttpConfig baseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    /**
     * 设置hostname
     *
     * @param hostname hostname
     * @return HeadersConfig
     */
    public HttpConfig hostname(String hostname) {
        this.hostname = hostname;
        return this;
    }

    /**
     * 设置ip
     *
     * @param ipaddress ip地址
     * @return HeadersConfig
     */
    public HttpConfig ipaddress(String ipaddress) {
        this.ipaddress = ipaddress;
        return this;
    }

    /**
     * 设置port
     *
     * @param port 端口号
     * @return HeadersConfig
     */
    public HttpConfig port(Integer port) {
        this.port = port;
        return this;
    }

    /**
     * 加签验证，常接于url后面
     *
     * @param ak 账号
     * @param sk 密码
     * @return HeadersConfig
     */
    public HttpConfig sign(String ak, String sk) {
        sign.put(ak, sk);
        return this;
    }

    @Override
    public String toString() {
        return "HttpConfig{" +
                "baseUrl='" + baseUrl + '\'' +
                ", hostname='" + hostname + '\'' +
                ", ipaddress='" + ipaddress + '\'' +
                ", port=" + port +
                ", sign=" + sign +
                ", requestHeaders=" + requestHeaders +
                '}';
    }
}
