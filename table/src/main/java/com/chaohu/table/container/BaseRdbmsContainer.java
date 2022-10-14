package com.chaohu.table.container;

import com.chaohu.table.table.DbTable;
import com.chaohu.table.table.ITable;
import com.chaohu.table.ContainerConstants;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author wangmin
 */
public abstract class BaseRdbmsContainer extends BaseContainer {

    protected BaseRdbmsContainer(BaseBuilder<?, ?> builder) {
        super(builder);
    }

    /**
     * 链接
     *
     * @return Statement
     */
    abstract Statement connect();

    @Override
    public boolean init() {
        Statement statement = connect();
        if (statement == null) {
            return false;
        }
        try {
            logger.info("sql is：{}", getPath());
            if (getPath().contains(ContainerConstants.SELECT)) {
                ResultSet rs = statement.executeQuery(getPath());
                while (rs.next()) {
                    String tableName = rs.getString(1);
                    ITable table = new DbTable.Builder().path(getPath()).name(tableName).statement(statement).build();
                    addTable(table);
                }
            } else {
                ITable table = new DbTable.Builder().path(getPath()).statement(statement).build();
                addTable(table);
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
