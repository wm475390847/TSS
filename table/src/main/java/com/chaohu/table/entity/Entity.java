package com.chaohu.table.entity;

import com.chaohu.table.row.IRow;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author wangmin
 */
public class Entity extends BaseEntity<String, IFactory> {
    private final String sql;

    protected Entity(Builder builder) {
        super(builder);
        this.sql = builder.sql;
    }

    @Override
    public String getSql() {
        return this.sql;
    }

    @Override
    public IRow refresh() {
        IEntity<?, ?>[] entities = getFactory().create(this.getSql());
        IRow row = entities[0].getCurrent();
        saveNewRow(row);
        return getCurrent();
    }

    @Setter
    @Accessors(chain = true, fluent = true)
    public static class Builder extends BaseBuilder<Entity, Factory, Builder> {
        private String sql;

        @Override
        protected Entity buildEntity() {
            return new Entity(this);
        }
    }
}
