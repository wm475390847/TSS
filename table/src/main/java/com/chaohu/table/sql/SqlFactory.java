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
public class SqlFactory<T> {
    private final String configPath;
    private final Class<T> tClass;

    public SqlFactory(Builder builder) {
        this.configPath = builder.configPath;
        this.tClass = (Class<T>) builder.tClass;
    }

    private SqlSession sqlSession = null;

    public T execute() {
        try {
            InputStream inputStream = Resources.getResourceAsStream(configPath);
            SqlSessionManager sqlSessionManager = SqlSessionManager.newInstance(inputStream);
            sqlSession = sqlSessionManager.openSession(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sqlSession.getMapper(tClass);
    }

    @Setter
    @Accessors(chain = true, fluent = true)
    public static class Builder {

        private String configPath = "configuration.xml";
        private Object tClass;

        public SqlFactory<Object> build() {
            return new SqlFactory<>(this);
        }
    }
}
