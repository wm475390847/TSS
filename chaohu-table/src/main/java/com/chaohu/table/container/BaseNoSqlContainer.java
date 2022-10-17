package com.chaohu.table.container;

import com.alicloud.openservices.tablestore.SyncClient;
import com.chaohu.table.table.ITable;
import com.chaohu.table.table.OtsTable;
import org.apache.commons.lang3.StringUtils;

/**
 * nosql容器
 *
 * @author wangmin
 * @data 2021-06-15
 */
public abstract class BaseNoSqlContainer extends BaseContainer {

    public BaseNoSqlContainer(BaseBuilder<?, ?> baseBuilder) {
        super(baseBuilder);
    }

    /**
     * 获取同步客户端
     *
     * @return SyncClient
     */
    abstract SyncClient getSyncClient();

    @Override
    public boolean init() {
        SyncClient syncClient = getSyncClient();
        if (syncClient == null) {
            return false;
        }
        logger.info("tableName:{}", getPath());
        if (!StringUtils.isEmpty(getPath())) {
            ITable table = new OtsTable.Builder().path(getPath()).name(getPath()).syncClient(syncClient).buildTable();
            addTable(table);
            return true;
        }
        return false;
    }
}
