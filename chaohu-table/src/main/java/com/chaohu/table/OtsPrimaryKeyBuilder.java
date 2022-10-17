package com.chaohu.table;

import lombok.Getter;

/**
 * ots主键构造器
 *
 * @author wangmin
 * @data 2021-06-18
 */
@Getter
public class OtsPrimaryKeyBuilder implements IKeyBuilder {

    private OtsPrimaryKey inclusiveStartPrimaryKey;
    private OtsPrimaryKey exclusiveEndPrimaryKey;

    public OtsPrimaryKeyBuilder inclusiveStartPrimaryKey(OtsPrimaryKey inclusiveStartPrimaryKey) {
        this.inclusiveStartPrimaryKey = inclusiveStartPrimaryKey;
        return this;
    }

    public OtsPrimaryKeyBuilder exclusiveEndPrimaryKey(OtsPrimaryKey exclusiveEndPrimaryKey) {
        this.exclusiveEndPrimaryKey = exclusiveEndPrimaryKey;
        return this;
    }
}
