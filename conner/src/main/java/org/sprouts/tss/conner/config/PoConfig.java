package org.sprouts.tss.conner.config;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author wangmin18
 * @date 2023/12/29 11:25
 */
@Getter
public class PoConfig extends AbstractInformConfig {
    private final String webhook;
    private final String keyword;

    public PoConfig(Builder builder) {
        super(builder);
        this.keyword = builder.keyword;
        this.webhook = builder.webhook;
    }

    @Setter
    @Accessors(chain = true, fluent = true)
    public static class Builder extends AbstractInformBuilder {
        private String webhook;
        private String keyword;

        @Override
        protected AbstractInformConfig buildInform() {
            return new PoConfig(this);
        }
    }
}
