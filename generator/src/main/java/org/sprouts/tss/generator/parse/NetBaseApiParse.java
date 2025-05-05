package org.sprouts.tss.generator.parse;

import org.sprouts.tss.generator.pojo.Constant;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 网络解析抽象解析类
 *
 * @author wangmin
 * @date 2022/5/17 13:05
 */
@Slf4j
@Getter
public abstract class NetBaseApiParse<T> extends BaseApiParse<T> {
    private final String basePath;

    public NetBaseApiParse(NetBaseBuilder<?, ?, ?> baseBuilder) {
        super(baseBuilder);
        this.basePath = baseBuilder.basePath;
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
    protected abstract static class NetBaseBuilder<P extends IParse<R>, B extends BaseBuilder<P, B, R>, R> extends BaseBuilder<P, B, R> {
        private String basePath;

        public B token(String token) {
            Constant.TOKEN = token;
            return self();
        }

        public B host(String host) {
            Constant.HOST = host;
            return self();
        }

        public B baseUrl(String baseUrl) {
            Constant.BASE_URL = baseUrl;
            return self();
        }

        public B basePath(String basePath) {
            this.basePath = basePath;
            return self();
        }

        public P build() {
            return buildNetParse();
        }


        /**
         * 构建解析
         *
         * @return IParse
         */
        protected abstract P buildNetParse();
    }
}
