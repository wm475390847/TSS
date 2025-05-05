package org.sprouts.tss.robot;

import com.alibaba.fastjson.JSONObject;
import io.restassured.response.Response;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import static io.restassured.RestAssured.given;

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
            log.info("==> 发送钉钉通知");
            if (StringUtils.isEmpty(webhook)) {
                log.info("<== webhook为空");
                return;
            }
            Response response = given()
                    .header("Content-Type", "application/json")
                    .body(message())
                    .post(webhook)
                    .then()
                    .extract()
                    .response();
            if (response.body() != null) {
                log.info("==> response: {}", response.body().asString());
                log.info("<== success");
            } else {
                log.info("<== fail");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
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
