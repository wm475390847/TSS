package org.sprouts.tss.table.table;

import org.sprouts.tss.table.IKeyBuilder;
import org.sprouts.tss.table.property.BaseProperty;
import org.sprouts.tss.table.row.IRow;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * @author wangmin
 */
@Getter
public abstract class BaseTable extends BaseProperty implements ITable {
    private final Map<String, IRow> rows = new LinkedHashMap<>();
    private final Map<IRow, Integer> rowsCount = new LinkedHashMap<>();

    @Setter
    private String path;

    @Setter
    private IKeyBuilder keyBuilder;

    protected BaseTable(BaseBuilder<?, ?> baseBuilder) {
        super(baseBuilder);
        this.path = baseBuilder.path;
    }

    /**
     * 加载方法
     *
     * @return 是否成功
     */
    @Override
    public abstract boolean load();

    @Override
    public boolean addRow(IRow row) {
        if (row != null) {
            row.init();
            if (rows.containsKey(row.getKey())) {
                int currCount = rowsCount.get(row);
                int count = currCount + 1;
                rowsCount.put(row, count);
            } else {
                if (row.getKey() != null) {
                    rows.put(row.getKey(), row);
                    rowsCount.put(row, 1);
                } else {
                    int currCount = rows.size();
                    int count = currCount + 1;
                    rows.put(String.valueOf(count), row);
                    rowsCount.put(row, count);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public IRow findRow(String key) {
        return StringUtils.isEmpty(key) ? null : this.rows.get(key.toLowerCase());
    }

    @Override
    public IRow[] getRows() {
        return new LinkedList<>(this.rows.values()).toArray(new IRow[0]);
    }

    @Override
    public void clear() {
        this.rows.clear();
        this.rowsCount.clear();
    }

    public abstract static class BaseBuilder<T extends BaseBuilder<?, ?>, R extends BaseTable>
            extends BaseProperty.BaseBuilder<T, R> {
        private String path;

        public T path(String path) {
            this.path = path;
            return (T) this;
        }

        @Override
        protected R buildProperty() {
            return buildTable();
        }

        /**
         * 构建table
         *
         * @return R
         */
        public abstract R buildTable();
    }
}
