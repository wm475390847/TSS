package org.sprouts.tss.table.table;

import org.sprouts.tss.table.exception.TableException;
import org.sprouts.tss.table.field.IField;
import org.sprouts.tss.table.field.SimpleField;
import org.sprouts.tss.table.row.IRow;
import org.sprouts.tss.table.row.SimpleRow;
import org.sprouts.tss.table.ContainerConstants;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

/**
 * @author wangmin
 */
@Getter
public class DbTable extends BaseTable {
    private final Statement statement;

    public DbTable(Builder builder) {
        super(builder);
        this.statement = builder.statement;
    }

    @Override
    public boolean load() {
        String sql = !StringUtils.isEmpty(getPath()) ? getPath() : String.format(ContainerConstants.DB_TABLE_DEFAULT_SQL, getKey());
        if (statement != null) {
            try {
                if (sql.contains(ContainerConstants.UPDATE) || sql.contains(ContainerConstants.DELETE)) {
                    statement.executeUpdate(sql);
                    return true;
                }
                if (sql.contains(ContainerConstants.INSERT)) {
                    statement.execute(sql);
                    return true;
                }
                ResultSet rs = statement.executeQuery(sql);
                clear();
                ResultSetMetaData md = rs.getMetaData();
                int count = md.getColumnCount();
                int index = 1;
                while (rs.next()) {
                    IRow row = new SimpleRow.Builder().index(index++).build();
                    for (int i = 1; i <= count; i++) {
                        String columnName = md.getColumnName(i);
                        Object rsValue = rs.getObject(i);
                        String value = (rsValue == null) ? null : String.valueOf(rsValue);
                        IField field = new SimpleField.Builder().name(columnName).value(value).build();
                        row.addField(field);
                    }
                    addRow(row);
                }
                return true;
            } catch (Exception e) {
                throw new TableException("数据库表加载错误：" + e.getMessage());
            }
        } else {
            throw new TableException("数据库表加载错误：statement为空，数据库没有初始化");
        }
    }

    @Setter
    @Accessors(chain = true, fluent = true)
    public static class Builder extends BaseBuilder<Builder, DbTable> {
        private Statement statement;

        @Override
        public DbTable buildTable() {
            return new DbTable(this);
        }
    }
}
