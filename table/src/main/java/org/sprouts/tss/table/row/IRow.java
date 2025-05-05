package org.sprouts.tss.table.row;

import org.sprouts.tss.table.field.IField;
import org.sprouts.tss.table.property.IProperty;

/**
 * @author wangmin
 */
public interface IRow extends IProperty {

    /**
     * 完成对数据的初始化操作
     *
     * @return IRow
     */
    IRow init();

    /**
     * 增加一个字段
     *
     * @param field 字段
     * @return boolean
     */
    boolean addField(IField field);

    /**
     * 获取所有字段
     *
     * @return IField[] 字段列表
     */
    IField[] getFields();

    /**
     * 获取所有字段的标识符列表
     *
     * @return String[] 标识符列表
     */
    String[] getFieldsKey();

    /**
     * 获取所有字段值的列表
     *
     * @return String[] 字段值列表
     */
    String[] getFieldsValue();

    /**
     * 获取指定的字段
     *
     * @param key 标识符，大小写不敏感
     * @return IField 如果存在此字段，返回对象，否则返回null
     */
    IField findField(String key);

    /**
     * 查找字段 ，支持模糊查找
     *
     * @param name 字段名，支持正则表达式
     * @return IField[]
     */
    IField[] findFields(String name);

    /**
     * 判断行数据是否包含指定键
     *
     * @param keyName 键名
     * @return boolean
     */
    boolean containsKey(String keyName);

    /**
     * 获取索引值
     *
     * @return 值
     */
    int getIndex();

}
