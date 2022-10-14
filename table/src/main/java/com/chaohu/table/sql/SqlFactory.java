package com.chaohu.table.sql;

import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;

import java.io.IOException;
import java.io.InputStream;

/**
 * sql工厂
 *
 * @author wangmin
 */
public class SqlFactory {
    private final String configPath;

    public SqlFactory(Builder builder) {
        this.configPath = builder.configPath;
    }

    private SqlSession sqlSession = null;

    public SqlFactory execute() {
        try {
            InputStream inputStream = Resources.getResourceAsStream(configPath);
            SqlSessionManager sqlSessionManager = SqlSessionManager.newInstance(inputStream);
            sqlSession = sqlSessionManager.openSession(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public <T extends MyBatisBaseDao<?, ?>> T call(Class<T> t) {
        return sqlSession.getMapper(t);
    }

    @Setter
    @Accessors(chain = true, fluent = true)
    public static class Builder {

        /**
         * 配置自己的sqlConfiguration.xml
         */
        private String configPath = "configuration.xml";

        public SqlFactory build() {
            return new SqlFactory(this);
        }
    }
}
