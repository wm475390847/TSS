package com.chaohu.table.property;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wangmin
 */
@Getter
public abstract class BaseProperty implements IProperty {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String key;
    private String value;

    protected BaseProperty(BaseBuilder<?, ?> baseBuilder) {
        this.key = baseBuilder.name;
        this.value = baseBuilder.value;
    }

    @Override
    public String toString() {
        return "BaseProperty{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

    @Override
    public void setValue(String value) {
        this.value = value == null ? "" : value;
    }

    public abstract static class BaseBuilder<T, R> {
        private String name;
        private String value;

        public T name(String name) {
            this.name = name;
            return (T) this;
        }

        public T value(String value) {
            this.value = value;
            return (T) this;
        }

        public R build() {
            return buildProperty();
        }

        /**
         * 构建属性
         *
         * @return R
         */
        protected abstract R buildProperty();
    }
}
