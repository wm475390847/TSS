package org.sprouts.tss.table.row;

/**
 * @author wangmin
 */
public class SimpleRow extends BaseRow {
    protected SimpleRow(Builder builder) {
        super(builder);
    }

    @Override
    public IRow init() {
        return this;
    }

    public static class Builder extends BaseBuilder<Builder, SimpleRow> {

        @Override
        protected SimpleRow buildRow() {
            return new SimpleRow(this);
        }
    }
}
