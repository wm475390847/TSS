package org.sprouts.tss.conner.config;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Arrays;

/**
 * 钉钉配置
 *
 * @author wangmin
 * @date 2022/5/18 11:27
 */
@Getter
public class DingConfig extends AbstractInformConfig {

    private final String webhook;
    private final String keyword;
    private final String[] phones;
    private final boolean isAtAll;

    public DingConfig(Builder builder) {
        super(builder);
        this.webhook = builder.webhook;
        this.phones = builder.phones;
        this.isAtAll = builder.isAtAll;
        this.keyword = builder.keyword;
    }

    @Setter
    @Accessors(chain = true, fluent = true)
    public static class Builder extends AbstractInformBuilder {
        private String[] phones;
        private String webhook;
        private String keyword;
        private boolean isAtAll;

        private Builder phones(String... phones) {
            this.phones = phones.clone();
            return this;
        }

        @Override
        public AbstractInformConfig buildInform() {
            return new DingConfig(this);
        }
    }

    @Override
    public String toString() {
        return "DingDingConfig{" +
                "webhook='" + webhook + '\'' +
                ", keyword='" + keyword + '\'' +
                ", phones=" + Arrays.toString(phones) +
                ", isAtAll=" + isAtAll +
                '}';
    }
}
