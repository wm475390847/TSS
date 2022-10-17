package com.chaohu.conner.config;

import lombok.Getter;

/**
 * @author wangmin
 * @date 2022/5/18 11:27
 */
@Getter
public class ProductConfig extends AbstractConfig {

    private String caseOwner;
    private String productName;
    private Integer productId;

    public ProductConfig caseOwner(String caseOwner) {
        this.caseOwner = caseOwner;
        return this;
    }

    public ProductConfig productName(String productName) {
        this.productName = productName;
        return this;
    }

    public ProductConfig productId(Integer productId) {
        this.productId = productId;
        return this;
    }
}
