package com.chaohu.generate.parse;

import com.chaohu.generate.pojo.Constant;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 抽象解析类
 *
 * @author wangmin
 * @date 2022/5/17 13:05
 */
@Slf4j
@Getter
public abstract class BaseApiParse<T> implements IParse<T> {
    private final String basePath;

    public BaseApiParse(BaseBuilder<?, ?, ?> baseBuilder) {
        this.basePath = baseBuilder.basePath;
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

    protected abstract static class BaseBuilder<B extends BaseBuilder<?, ?, ?>, K, T> {
        private String basePath;

        public B token(String token) {
            Constant.TOKEN = token;
            return (B) this;
        }

        public B host(String host) {
            Constant.HOST = host;
            return (B) this;
        }

        public B baseUrl(String baseUrl) {
            Constant.BASE_URL = baseUrl;
            return (B) this;
        }

        public B basePath(String basePath) {
            this.basePath = basePath;
            return (B) this;
        }

        public IParse<T> build() {
            return buildParse();
        }

        /**
         * 构建解析
         *
         * @return IParse
         */
        protected abstract IParse<T> buildParse();
    }
}
