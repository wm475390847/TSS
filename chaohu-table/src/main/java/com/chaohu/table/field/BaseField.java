package com.chaohu.table.field;

import com.chaohu.table.property.BaseProperty;
import lombok.Getter;

/**
 * @author wangmin
 */
@Getter
public abstract class BaseField extends BaseProperty implements IField {

    protected BaseField(BaseBuilder<?, ?> baseBuilder) {
        super(baseBuilder);
    }

    public abstract static class BaseBuilder<T extends BaseBuilder<?, ?>, R extends BaseField>
            extends BaseProperty.BaseBuilder<T, R> {

        @Override
        protected R buildProperty() {
            return buildField();
        }

        /**
         * 构建字段
         *
         * @return R
         */
        protected abstract R buildField();
    }
}
