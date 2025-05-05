package org.sprouts.tss.conner.config;

import org.sprouts.tss.conner.AbstractMessage;
import lombok.Getter;

/**
 * 通知类配置抽象类
 *
 * @author wangmin18
 * @date 2023/12/29 11:22
 */
@Getter
public abstract class AbstractInformConfig extends AbstractConfig {

    protected final AbstractMessage messageFormat;

    protected AbstractInformConfig(AbstractInformBuilder builder) {
        this.messageFormat = builder.messageFormat;
    }

    public abstract static class AbstractInformBuilder {
        private AbstractMessage messageFormat;

        public AbstractInformBuilder messageFormat(AbstractMessage messageFormat) {
            this.messageFormat = messageFormat;
            return this;
        }

        public AbstractInformConfig build() {
            return buildInform();
        }

        /**
         * 构建方法，子类实现
         *
         * @return InformConfig
         */
        protected abstract AbstractInformConfig buildInform();
    }
}
