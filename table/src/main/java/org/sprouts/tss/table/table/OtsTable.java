package org.sprouts.tss.table.table;

import com.alicloud.openservices.tablestore.SyncClient;
import com.alicloud.openservices.tablestore.model.Column;
import com.alicloud.openservices.tablestore.model.PrimaryKeyBuilder;
import com.alicloud.openservices.tablestore.model.RangeIteratorParameter;
import com.alicloud.openservices.tablestore.model.Row;
import org.sprouts.tss.table.field.IField;
import org.sprouts.tss.table.field.SimpleField;
import org.sprouts.tss.table.OtsPrimaryKey;
import org.sprouts.tss.table.OtsPrimaryKeyBuilder;
import org.sprouts.tss.table.row.IRow;
import org.sprouts.tss.table.row.SimpleRow;
import com.google.common.base.Preconditions;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Iterator;

/**
 * OTStable
 *
 * @author wangmin
 * @date 2021-06-16
 */
public class OtsTable extends BaseTable {
    private final SyncClient syncClient;
    private RangeIteratorParameter rangeIteratorParameter;

    protected OtsTable(Builder builder) {
        super(builder);
        this.syncClient = builder.syncClient;
    }

    @Override
    public boolean load() {
        init();
        int index = 1;
        try {
            Iterator<Row> iterator = syncClient.createRangeIterator(rangeIteratorParameter);
            logger.info(">>>>>>开始收集结果");
            while (iterator.hasNext()) {
                IRow otsRow = new SimpleRow.Builder().index(index++).build();
                Row row = iterator.next();
                for (Column column : row.getColumns()) {
                    String name = column.getName();
                    String value = column.getValue().toString();
                    IField field = new SimpleField.Builder().name(name).value(value).build();
                    otsRow.addField(field);
                }
                addRow(otsRow);
            }
            logger.info("<<<<<<结果收集完毕");
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void init() {
        OtsPrimaryKeyBuilder otsPrimaryKeyBuilder = (OtsPrimaryKeyBuilder) getKeyBuilder();
        Preconditions.checkNotNull(otsPrimaryKeyBuilder, "主键构造器为空");
        rangeIteratorParameter = new RangeIteratorParameter(getPath());

        PrimaryKeyBuilder builder = PrimaryKeyBuilder.createPrimaryKeyBuilder();
        OtsPrimaryKey inclusiveStartPrimaryKey = otsPrimaryKeyBuilder.getInclusiveStartPrimaryKey();
        inclusiveStartPrimaryKey.getOtsPrimaryKey().forEach(builder::addPrimaryKeyColumn);
        rangeIteratorParameter.setInclusiveStartPrimaryKey(builder.build());
        logger.info("StartPrimaryKey:{}", rangeIteratorParameter.getInclusiveStartPrimaryKey().toString());

        builder = PrimaryKeyBuilder.createPrimaryKeyBuilder();
        OtsPrimaryKey otsPrimaryKey = otsPrimaryKeyBuilder.getExclusiveEndPrimaryKey();
        otsPrimaryKey.getOtsPrimaryKey().forEach(builder::addPrimaryKeyColumn);
        rangeIteratorParameter.setExclusiveEndPrimaryKey(builder.build());
        logger.info("EndPrimaryKey:{}", rangeIteratorParameter.getExclusiveEndPrimaryKey().toString());

        rangeIteratorParameter.setMaxVersions(1);
    }

    @Setter
    @Accessors(chain = true, fluent = true)
    public static class Builder extends BaseBuilder<Builder, OtsTable> {

        private SyncClient syncClient;

        @Override
        public OtsTable buildTable() {
            return new OtsTable(this);
        }
    }

}
