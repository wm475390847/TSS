package com.chaohu.conner.base;

import com.chaohu.conner.AbstractCollector;
import com.chaohu.conner.AbstractMessage;
import com.chaohu.conner.Context;
import com.chaohu.conner.config.DingDingConfig;
import com.chaohu.conner.config.ProductConfig;
import lombok.extern.slf4j.Slf4j;
import org.testng.ITestNGMethod;

/**
 * 用例收集器
 *
 * @author wangmin
 * @date 2022/5/27 10:01
 */
@Slf4j
public class CaseCollector extends AbstractCollector {

    @Override
    protected Object initInfo(ITestNGMethod method, String env, ProductConfig config) {
        CaseInfo caseInfo = new CaseInfo()
                .setCaseDesc(method.getDescription())
                .setCaseName(method.getMethodName())
                .setEnv(env);
        if (config != null) {
            caseInfo.setCaseOwner(config.getCaseOwner())
                    .setProductId(config.getProductId());
        }
        return caseInfo;
    }

    @Override
    protected void saveInfo(Object info, String error) {
        if (Context.debug.trim().equalsIgnoreCase(Context.isOnDebug)) {
            return;
        }
        CaseInfo caseInfo = (CaseInfo) info;
        caseInfo = error == null ? caseInfo.setCaseResult(true).setCaseReason("PASS")
                : caseInfo.setCaseResult(false).setCaseReason(error)
                .setDetailMsg(Context.detailMessage)
                .setFailApi(Context.failApi);
        insertToCaseInfo(caseInfo);
    }

    /**
     * 插入caseInfo表
     *
     * @param caseInfo case信息
     */
    private synchronized void insertToCaseInfo(CaseInfo caseInfo) {
//        if (caseInfo == null) {
//            log.info("caseInfo is null");
//            return;
//        }
//        log.info("==> 保存数据");
//        CaseInfoMapper caseInfoMapper = new SqlFactory.Builder().configPath("ttp.xml")
//                .build().execute().call(CaseInfoMapper.class);
//        // 查询是否存在同名用例
//        List<CaseInfo> caseInfos = caseInfoMapper.selectByCaseNameAndEnv(caseInfo);
//        if (caseInfos.size() != 0) {
//            // 存在更新
//            Long id = caseInfos.get(0).getId();
//            caseInfo.setId(id).setExecuteTime(new Date());
//            int updateByPrimaryKeySelective = caseInfoMapper.updateByPrimaryKeySelective(caseInfo);
//            if (updateByPrimaryKeySelective != 0) {
//                log.info("<== 更新成功");
//            }
//        } else {
//            // 不存在新增
//            int insertSelective = caseInfoMapper.insertSelective(caseInfo);
//            if (insertSelective != 0) {
//                log.info("<== 新增成功");
//            }
//        }
    }

    @Override
    protected void sendInform(Object info, DingDingConfig config, Throwable throwable) {
        if (Context.debug.trim().equalsIgnoreCase(Context.isOnDebug)) {
            return;
        }
        CaseInfo caseInfo = (CaseInfo) info;
        caseInfo.setCaseReason(throwable.getMessage());
        AbstractMessage messageFormat = config.getMessageFormat();
        String format = messageFormat.getFormat(info);
        System.err.println(format);
//        new MarkdownRobot.Builder()
//                .text(format)
//                .mobiles(config.getPhones())
//                .keyword(config.getKeyword())
//                .webhook(config.getWebhook())
//                .build()
//                .send();
    }
}
