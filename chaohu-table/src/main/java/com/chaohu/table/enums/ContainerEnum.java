package com.chaohu.table.enums;

import com.chaohu.table.container.DbContainer;
import com.chaohu.table.container.ExcelContainer;
import com.chaohu.table.container.IContainer;
import lombok.Getter;

/**
 * 容器类型枚举
 *
 * @author wangmin
 */
public enum ContainerEnum {
    /**
     * 数据库地址
     */
    DB_BUSINESS_PORSCHE(new DbContainer.Builder().driverName("com.mysql.cj.jdbc.Driver")
            .jdbcUrl("")
            .password("").username("").build()),

    EXCEL(new ExcelContainer.Builder().buildContainer()),
    ;

    ContainerEnum(IContainer container) {
        this.container = container;
    }

    @Getter
    private final IContainer container;
}
