package org.sprouts.tss.table.field;

/**
 * @author wangmin
 */
public class SimpleField extends BaseField {

    protected SimpleField(Builder builder) {
        super(builder);
    }

    public static class Builder extends BaseBuilder<Builder, SimpleField> {

        @Override
        protected SimpleField buildField() {
            return new SimpleField(this);
        }
    }
}
