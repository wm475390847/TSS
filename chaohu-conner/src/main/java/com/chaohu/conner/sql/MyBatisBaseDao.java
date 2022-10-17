package com.chaohu.conner.sql;

import java.io.Serializable;

/**
 * DAO公共基类，由MybatisGenerator自动生成请勿修改
 *
 * @param <Model> The Model Class 这里是泛型不是Model类
 * @param <PK>    The Primary Key Class 如果是无主键，则可以用Model来跳过，如果是多主键则是Key类
 * @author wangmin
 */
public interface MyBatisBaseDao<Model, PK extends Serializable> {
    /**
     * 通过主键删除
     *
     * @param id 主键
     * @return 数量
     */
    int deleteByPrimaryKey(PK id);

    /**
     * 插入
     *
     * @param record model
     * @return 数量
     */
    int insert(Model record);

    /**
     * 选择性插入
     *
     * @param record model
     * @return 数量
     */
    int insertSelective(Model record);

    /**
     * 通过主键查找
     *
     * @param id 主键
     * @return model
     */
    Model selectByPrimaryKey(PK id);

    /**
     * 通过主键选择性更新
     *
     * @param record model
     * @return 数量
     */
    int updateByPrimaryKeySelective(Model record);

    /**
     * 通过主键更新
     *
     * @param record model
     * @return 数量
     */
    int updateByPrimaryKey(Model record);
}