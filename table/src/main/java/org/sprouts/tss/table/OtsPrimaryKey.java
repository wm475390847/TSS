package org.sprouts.tss.table;

import com.alicloud.openservices.tablestore.model.PrimaryKeyValue;
import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * ots主键，包含主键名和主键值
 *
 * @author wangmin
 * @date 2021-06-18
 */
@Getter
public class OtsPrimaryKey {
    private final Map<String, PrimaryKeyValue> otsPrimaryKey;

    public OtsPrimaryKey(Builder builder) {
        this.otsPrimaryKey = builder.map;
    }

    public static class Builder {
        private final Map<String, PrimaryKeyValue> map = new LinkedHashMap<>();

        public Builder primaryKey(String key, PrimaryKeyValue value) {
            this.map.put(key, value);
            return this;
        }

        public boolean containsKey(String key) {
            return map.containsKey(key);
        }

        public OtsPrimaryKey build() {
            return new OtsPrimaryKey(this);
        }
    }

    @Override
    public String toString() {
        return "OTSPrimaryKey{" +
                "otsPrimaryKey=" + otsPrimaryKey +
                '}';
    }
}
