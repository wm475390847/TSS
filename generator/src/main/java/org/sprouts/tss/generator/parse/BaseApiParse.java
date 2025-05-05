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
public abstract class BaseApiParse<T> implements IParse<T> {
    private final String suffix;

    BaseApiParse(BaseBuilder<?, ?, ?> baseBuilder) {
        this.suffix = baseBuilder.suffix;
    }

    @Override
    public List<T> execute() {
        log.info("开始解析，请稍候...");
        long startTime = System.currentTimeMillis();
        List<T> list = getList();
        log.info("解析完毕，耗时: {}ms", System.currentTimeMillis() - startTime);
        return list;
    }

    /**
     * 获取解析后的集合
     *
     * @return 集合
     */
    protected abstract List<T> getList();

    protected abstract static class BaseBuilder<P extends IParse<R>, B extends BaseBuilder<P, B, R>, R> {
        private String suffix = "Api";

        public B suffix(String suffix) {
            this.suffix = suffix;
            return self();
        }

        public P build() {
            return buildNetParse();
        }

        protected abstract B self();

        /**
         * 构建解析
         *
         * @return IParse
         */
        protected abstract P buildNetParse();
    }
}
