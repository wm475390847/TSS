package org.sprouts.tss.conner.http.client;

import okhttp3.Dns;
import okhttp3.internal.annotations.EverythingIsNonNull;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;

/**
 * 自定义 DNS 解析
 * 指定特定主机名解析到特定的 IP 地址
 *
 * @author wangmin
 */
@EverythingIsNonNull
public class CustomDns implements Dns {
    private final String hostname;
    private final String ipaddress;

    public CustomDns(String hostname, String ipaddress) {
        this.hostname = hostname;
        this.ipaddress = ipaddress;
    }

    @Override
    public List<InetAddress> lookup(String hostname) throws UnknownHostException {
        if (hostname.equals(this.hostname)) {
            // 模拟将 "example.com" 解析为特定的 IP 地址
            return Collections.singletonList(InetAddress.getByName(ipaddress));
        }

        // 对于其他主机名，使用默认的 DNS 解析
        return Dns.SYSTEM.lookup(hostname);
    }
}
