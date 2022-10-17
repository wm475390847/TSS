package com.chaohu.table.container;

import com.chaohu.table.property.IProperty;
import com.chaohu.table.table.ITable;

/**
 * 容器接口
 * 每种类型对应一种容器：db、excel
 *
 * @author wangmin
 * @data 2021-05-11
 */
public interface IContainer extends IProperty {

    /**
     * 初始化
     *
     * @return boolean
     */
    boolean init();

    /**
     * 获取文件路径或者sql
     *
     * @return String 路径
     */
    String getPath();

    /**
     * 放入路径
     *
     * @param path 路径
     */
    void setPath(String path);

    /**
     * 添加一个表
     *
     * @param table 表
     * @return boolean
     */
    boolean addTable(ITable table);

    /**
     * 获取所有表
     *
     * @return tables
     */
    ITable[] getTables();

    /**
     * 查询表
     *
     * @param tableName 表名
     * @return table
     */
    ITable findTable(String tableName);

    /**
     * 查询表集合
     *
     * @param tableName 表名
     * @return tables
     */
    ITable[] findTables(String tableName);

    /**
     * 写入
     *
     * @return 是否写入成功
     */
    default boolean write() {
        return false;
    }
}
