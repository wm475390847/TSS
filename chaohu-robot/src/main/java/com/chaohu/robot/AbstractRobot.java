package com.chaohu.robot;

import com.alibaba.fastjson.JSONObject;
import com.chaohu.conner.http.Api;
import com.chaohu.conner.http.MethodEnum;
import com.chaohu.conner.http.connector.IConnector;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

/**
 * @author wangmin
 * @date 2022/3/12 11:31 上午
 */
@Slf4j
public abstract class AbstractRobot implements IRobot {

    private final String webhook;

    @Getter
    private final String keyword;

    public AbstractRobot(BaseBuilder baseBuilder) {
        this.webhook = baseBuilder.webhook;
        this.keyword = baseBuilder.keyword;
    }

    @Override
    public void send() {
        try {
            Api api = new Api.Builder()
                    .contentType("application/json")
                    .requestBody(message())
                    .method(MethodEnum.POST)
                    .url(webhook)
                    .build();
            if (StringUtils.isNotEmpty(webhook)) {
                IConnector<Response> connector = api.getMethodEnum().getConnector();
                Response response = connector.api(api).execute();
                log.info("==> 发送钉钉通知");
                if (response.body() != null) {
                    log.info("==> response: {}", response.body().string());
                    log.info("<== success");
                } else {
                    log.info("<== fail");
                }
            } else {
                log.info("<== webhook为空");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 构建信息
     *
     * @return 信息
     */
    protected abstract JSONObject message();

    @Setter
    @Accessors(chain = true, fluent = true)
    public abstract static class BaseBuilder {

        private String webhook;
        private String keyword;

        /**
         * 构建
         *
         * @return IDingTalk
         */
        public abstract IRobot build();
    }
}
