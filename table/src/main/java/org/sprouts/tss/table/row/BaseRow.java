package org.sprouts.tss.table.row;

import org.sprouts.tss.table.field.IField;
import org.sprouts.tss.table.property.BaseProperty;
import org.sprouts.tss.table.property.IProperty;
import com.google.common.base.Preconditions;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * @author wangmin
 */
@Getter
public abstract class BaseRow extends BaseProperty implements IRow {
    private final Map<String, IField> fields = new LinkedHashMap<>();
    private final int index;

    protected BaseRow(BaseBuilder<?, ?> baseBuilder) {
        super(baseBuilder);
        this.index = baseBuilder.index;
    }

    /**
     * 初始化
     *
     * @return org.dragon.box.row
     */
    @Override
    public abstract IRow init();

    @Override
    public boolean addField(IField field) {
        if (field != null) {
            if (fields.containsKey(field.getKey())) {
                return true;
            }
            fields.put(field.getKey(), field);
            return true;
        }
        return false;
    }

    @Override
    public IField[] getFields() {
        return new LinkedList<>(this.fields.values()).toArray(new IField[0]);
    }

    @Override
    public String[] getFieldsKey() {
        return new LinkedList<>(this.fields.keySet()).toArray(new String[0]);
    }

    @Override
    public String[] getFieldsValue() {
        return this.fields.values().stream().map(IProperty::getValue).toArray(String[]::new);
    }

    @Override
    public IField findField(String key) {
        return StringUtils.isEmpty(key) ? null : this.fields.get(key);
    }

    @Override
    public IField[] findFields(String name) {
        Preconditions.checkArgument(StringUtils.isEmpty(name), "字段名称不能为空");
        return this.fields.values().stream().filter(iField -> iField.getValue().contains(name)).toArray(IField[]::new);
    }

    @Override
    public boolean containsKey(String keyName) {
        return this.fields.containsKey(keyName);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SimpleRow{fields=");
        int i = 0;
        for (String key : fields.keySet()) {
            sb.append(fields.get(key).getKey());
            sb.append("(");
            sb.append(fields.get(key).getValue());
            sb.append(")");
            if (i != fields.size() - 1) {
                sb.append(",");
            }
        }
        sb.append("}");
        sb.append(";");
        sb.append(super.toString());
        return sb.toString();
    }

    public abstract static class BaseBuilder<T extends BaseBuilder<?, ?>, R extends BaseRow>
            extends BaseProperty.BaseBuilder<T, R> {
        private Integer index;

        public T index(int index) {
            this.index = index;
            return (T) this;
        }

        @Override
        protected R buildProperty() {
            return buildRow();
        }

        /**
         * 构建行
         *
         * @return R
         */
        protected abstract R buildRow();
    }
}
