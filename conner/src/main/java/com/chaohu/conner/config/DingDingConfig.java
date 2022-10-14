package com.chaohu.conner.config;

import com.chaohu.conner.AbstractMessageFormat;
import lombok.Getter;

/**
 * @author wangmin
 * @date 2022/5/18 11:27
 */
@Getter
public class DingDingConfig extends AbstractConfig {

    private String webhook;
    private String keyword;
    private String[] phones;
    private boolean isAtAll;
    private AbstractMessageFormat messageFormat;

    public DingDingConfig messageFormat(AbstractMessageFormat messageFormat) {
        this.messageFormat = messageFormat;
        return this;
    }

    public DingDingConfig webhook(String webhook) {
        this.webhook = webhook;
        return this;
    }

    public DingDingConfig keyword(String keyword) {
        this.keyword = keyword;
        return this;
    }

    public DingDingConfig isAtAll(Boolean isAtAll) {
        this.isAtAll = isAtAll;
        return this;
    }

    public DingDingConfig phones(String... phones) {
        this.phones = phones.clone();
        return this;
    }
}
