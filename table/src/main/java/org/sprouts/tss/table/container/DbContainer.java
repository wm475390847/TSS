package org.sprouts.tss.table.container;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * @author wangmin
 */
@Getter
public class DbContainer extends BaseRdbmsContainer {
    private final String jdbcUrl;
    private final String username;
    private final String password;
    private final String driverName;
    private Connection connect;

    protected DbContainer(Builder builder) {
        super(builder);
        this.jdbcUrl = builder.jdbcUrl;
        this.username = builder.username;
        this.password = builder.password;
        this.driverName = builder.driverName;
    }

    @Override
    Statement connect() {
        try {
            Class.forName(driverName).newInstance();
            connect = DriverManager.getConnection(this.jdbcUrl, this.username, this.password);
            return connect.createStatement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Setter
    @Accessors(chain = true, fluent = true)
    public static class Builder extends BaseBuilder<Builder, DbContainer> {
        private String jdbcUrl;
        private String username;
        private String password;
        private String driverName;

        @Override
        public DbContainer buildContainer() {
            return new DbContainer(this);
        }
    }
}
