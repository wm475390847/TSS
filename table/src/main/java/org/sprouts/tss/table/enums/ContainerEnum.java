package org.sprouts.tss.table.enums;

import org.sprouts.tss.table.container.DbContainer;
import org.sprouts.tss.table.container.ExcelContainer;
import org.sprouts.tss.table.container.IContainer;
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
