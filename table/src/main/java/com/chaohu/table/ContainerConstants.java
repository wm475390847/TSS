package com.chaohu.table;

/**
 * @author wangmin
 */
public class ContainerConstants {

    /**
     * INSERT标识，增加一个空格，防止以关键词命名的开头字段
     */
    public static final String INSERT = "insert ";

    /**
     * DELETE标识，增加一个空格，防止以关键词命名的开头字段
     */
    public static final String DELETE = "delete ";

    /**
     * update标识，增加一个空格，防止以关键词命名的开头字段
     */
    public static final String UPDATE = "update ";

    /**
     * select标识，增加一个空格，防止以关键词命名的开头字段
     */
    public static final String SELECT = "select ";

    public static final int COLLECT_INIT_CAPACITY = 1024;

    public static final char CSV_DEFAULT_SEPARATOR = ',';

    /**
     * 数据库表默认sql
     */
    public static final String DB_TABLE_DEFAULT_SQL = "select * from %s limit 200;";
}
