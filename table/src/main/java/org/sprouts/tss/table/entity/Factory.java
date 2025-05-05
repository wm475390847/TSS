package org.sprouts.tss.table.entity;

import com.alibaba.fastjson.JSONObject;
import org.sprouts.tss.table.container.IContainer;
import org.sprouts.tss.table.row.IRow;
import org.sprouts.tss.table.table.CsvTable;
import org.sprouts.tss.table.table.ITable;
import org.sprouts.tss.table.util.FileUtil;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wangmin
 */
public class Factory implements IFactory {
    private final IContainer container;

    private Factory(Builder builder) {
        this.container = builder.container;
    }

    @Override
    public IEntity<?, ?>[] create(String sqlStr) {
        IRow[] rows = invoke(sqlStr);
        return createEntity(rows).toArray(new IEntity[0]);
    }

    @Override
    public IEntity<?, ?>[] createExcel(String path) {
        String relativePath = FileUtil.getResourcePath(path);
        return create(relativePath);
    }

    @Override
    public IEntity<?, ?>[] createCsv(String path) {
        String relativePath = FileUtil.getResourcePath(path);
        ITable table = new CsvTable.Builder().path(relativePath).buildTable();
        IRow[] rows = table.load() ? table.getRows() : new IRow[0];
        return createEntity(rows).toArray(new IEntity[0]);
    }

    public <T> List<T> toJavaObjectList(String sql, Class<T> aClass) {
        IRow[] rows = invoke(sql);
        return Arrays.stream(rows).map(this::createJb).map(e -> JSONObject.toJavaObject(e, aClass)).collect(Collectors.toList());
    }

    @Setter
    @Accessors(chain = true, fluent = true)
    public static class Builder {
        private IContainer container;

        public Factory build() {
            return new Factory(this);
        }

    }

    private IRow[] invoke(String path) {
        container.setPath(path);
        container.init();
        ITable table = container.getTables().length > 0 ? container.getTables()[0] : null;
        if (table == null) {
            return new IRow[0];
        }
        table.setPath(path);
        if (!table.load()) {
            return new IRow[0];
        }
        return table.getRows();
    }

    private List<IEntity<?, ?>> createEntity(IRow[] rows) {
        return Arrays.stream(rows).map(row -> new Entity.Builder().row(row).factory(this).buildEntity()).collect(Collectors.toList());
    }

    private JSONObject createJb(IRow row) {
        JSONObject object = new JSONObject();
        Arrays.stream(row.getFields()).forEach(field -> object.put(field.getKey(), row.findField(field.getKey()).getValue()));
        return object;
    }
}
