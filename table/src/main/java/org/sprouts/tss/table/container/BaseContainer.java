package org.sprouts.tss.table.container;

import org.sprouts.tss.table.property.BaseProperty;
import org.sprouts.tss.table.table.ITable;
import org.sprouts.tss.table.ContainerConstants;
import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * @author wangmin
 */
@Setter
public abstract class BaseContainer extends BaseProperty implements IContainer {
    private final Map<String, ITable> tables = new LinkedHashMap<>(ContainerConstants.COLLECT_INIT_CAPACITY);
    @Getter
    private String path;

    public BaseContainer(BaseBuilder<?, ?> baseBuilder) {
        super(baseBuilder);
        this.path = baseBuilder.path;
    }

    /**
     * 初始化
     *
     * @return boolean
     */
    @Override
    public abstract boolean init();

    @Override
    public ITable[] getTables() {
        return new LinkedList<>(tables.values()).toArray(new ITable[0]);
    }

    @Override
    public boolean addTable(ITable table) {
        if (table != null) {
            tables.put(table.getKey(), table);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public ITable[] findTables(String tableName) {
        Preconditions.checkArgument(StringUtils.isNotEmpty(tableName), "查询表名不能为空");
        return this.tables.values().stream().filter(e -> e.getKey().contains(tableName)).toArray(ITable[]::new);
    }

    @Override
    public ITable findTable(String tableName) {
        Preconditions.checkArgument(StringUtils.isNotEmpty(tableName), "查询表名不能为空");
        return this.tables.values().stream().filter(e -> e.getKey().equals(tableName)).findFirst().orElse(null);
    }

    @Override
    public void setPath(String path) {
        this.path = path;
    }

    public abstract static class BaseBuilder<T extends BaseBuilder<?, ?>, R extends IContainer>
            extends BaseProperty.BaseBuilder<T, R> {
        private String path;

        public T path(String path) {
            this.path = path;
            return (T) this;
        }


        @Override
        protected R buildProperty() {
            return buildContainer();
        }

        /**
         * 构建容器
         *
         * @return R
         */
        public abstract R buildContainer();
    }
}
