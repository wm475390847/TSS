package com.chaohu.table.entity;

import com.chaohu.table.row.IRow;

/**
 * 实体接口
 *
 * @param <S> sql类
 * @param <F> 工厂类
 * @author wangmin
 * @date 2021-05-11
 */
public interface IEntity<S, F> {

    /**
     * 获取当前实体
     *
     * @return IRow
     */
    IRow getCurrent();

    /**
     * 刷新实体
     *
     * @return org.dragon.box.row
     */
    IRow refresh();

    /**
     * 获取实体生成器
     *
     * @return R
     */
    F getFactory();

    /**
     * 获取sql
     *
     * @return String
     */
    S getSql();

    /**
     * 获取字段值
     *
     * @param fieldName 字段名
     * @return 字段值
     */
    String getFieldValue(String fieldName);

    /**
     * 取值方法
     *
     * @param fieldName 字段名
     * @return long
     */
    long getLongField(String fieldName);

    /**
     * 取值方法
     *
     * @param fieldName 字段名
     * @return int
     */
    int getIntField(String fieldName);

    /**
     * 取值方法
     *
     * @param fieldName 字段名
     * @return float
     */
    float getFloatField(String fieldName);

    /**
     * 取值方法
     *
     * @param fieldName 字段名
     * @return double
     */
    double getDoubleField(String fieldName);
}
