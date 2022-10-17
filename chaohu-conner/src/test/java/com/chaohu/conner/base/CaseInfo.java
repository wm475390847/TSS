package com.chaohu.conner.base;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * case_info
 *
 * @author wangmin
 */
@Data
@Accessors(chain = true)
public class CaseInfo implements Serializable {
    private Long id;

    /**
     * 用例名称
     */
    private String caseName;

    /**
     * 用例所有者
     */
    private String caseOwner;

    /**
     * 用例结果0：失败、1：成功
     */
    private Boolean caseResult;

    /**
     * 用例执行信息PASS：通过、其他为异常
     */
    private String caseReason;

    /**
     * 用例描述
     */
    private String caseDesc;

    /**
     * 这个此段需要存此用例覆盖的接口集合
     */
    private String apiJson;

    /**
     * 产品id
     */
    private Integer productId;

    /**
     * 执行时间
     */
    private Date executeTime;

    /**
     * 详细信息
     */
    private String detailMsg;

    /**
     * 发生错误的api
     */
    private String failApi;

    /**
     * 环境：test、dev、pre
     */
    private String env;

    private Date gmtCreate;

    private Date gmtModified;

    private static final long serialVersionUID = 1L;
}