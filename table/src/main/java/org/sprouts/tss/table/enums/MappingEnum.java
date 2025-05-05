package org.sprouts.tss.table.enums;

/**
 * @author 47539
 */
public interface MappingEnum<T, R> {

    /**
     * 查询字段
     *
     * @param field 字段
     * @return T
     */
    T findByField(R field);
}
