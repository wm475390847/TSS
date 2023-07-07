package com.chaohu.conner.base;

import com.chaohu.conner.AbstractMessage;
import com.chaohu.conner.util.TimeUtil;

import java.util.Date;

/**
 * 通用报错格式
 *
 * @author wangmin
 * @date 2022/5/31 10:19
 */
public class Message extends AbstractMessage {

    @Override
    public String getFormat(Object info) {
        CaseInfo caseInfo = (CaseInfo) info;
        String message = "- 时间：" + TimeUtil.dateToTimestamp(new Date(), TimeUtil.FORMAT_SECOND) + "\n\n"
//                + "- 环境：" + ProductEnum.findEnum(caseInfo.getProductId()).getName() + "-" + caseInfo.getEnv() + " \n\n"
                + "- 验证：" + caseInfo.getCaseDesc() + " \n\n"
                + "- 异常：<font color=\"#FF0000\">" + caseInfo.getCaseReason() + " </font>" + "\n\n";
        if (caseInfo.getFailApi() != null) {
            message = message + "- 接口：" + "\n\n"
                    + "> " + caseInfo.getFailApi() + "\n\n";
        }
        if (caseInfo.getDetailMsg() != null) {
            message = message + "- 详情：" + "\n\n"
                    + "------" + "\n\n"
                    + "- ###### " + caseInfo.getDetailMsg() + "\n\n";
        }
        return message;
    }
}