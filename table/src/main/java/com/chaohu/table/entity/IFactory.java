package com.chaohu.table.entity;

/**
 * 工厂接口
 *
 * @author wangmin
 * @date 2021-05-13
 */
public interface IFactory {

    /**
     * 创建db实体
     *
     * @param sqlStr sql语句
     * @return 结果实体
     */
    IEntity<?, ?>[] create(String sqlStr);

    /**
     * 创建Excel实体
     *
     * @param path 文件路径
     * @return 结果实体
     */
    IEntity<?, ?>[] createExcel(String path);

    /**
     * 创建Csv实体
     *
     * @param path 文件路径
     * @return 结果实体
     */
    IEntity<?, ?>[] createCsv(String path);
}
