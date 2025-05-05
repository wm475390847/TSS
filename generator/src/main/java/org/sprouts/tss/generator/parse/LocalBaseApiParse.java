package org.sprouts.tss.generator.parse;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 本地解析抽象解析类
 *
 * @author wangmin
 * @date 2022/5/17 13:05
 */
@Slf4j
@Getter
public abstract class LocalBaseApiParse<T> extends BaseApiParse<T> {
    private final String filePath;

    public LocalBaseApiParse(LocalBaseBuilder<?, ?, ?> localBaseBuilder) {
        super(localBaseBuilder);
        this.filePath = localBaseBuilder.filePath;
    }

    /**
     * 获取解析后的集合
     *
     * @return 集合
     */
    protected abstract List<T> getList();

    /**
     * @param <P> 解析器
     * @param <B> 解析器构建器
     * @param <R> 解析结果类型
     */
    protected static abstract class LocalBaseBuilder<P extends IParse<R>, B extends BaseBuilder<P, B, R>, R> extends BaseBuilder<P, B, R> {
        private String filePath;

        public B filePath(String filePath) {
            this.filePath = filePath;
            return self();
        }

        /**
         * 构建解析
         *
         * @return IParse
         */
        protected P buildNetParse() {
            return buildLocalParse();
        }

        protected abstract P buildLocalParse();
    }
}
