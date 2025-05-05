package org.sprouts.tss.conner.collector;

import org.sprouts.tss.conner.AnnoHandle;
import org.sprouts.tss.conner.annotation.Collector;
import org.sprouts.tss.conner.enums.CaseStepEnum;
import org.testng.ITestResult;

/**
 * 收集器接口
 */
public interface ICollector {

    /**
     * 初始化case，具体怎么实现由子类决定
     *
     * @param iTestResult 测试结果
     */
    Object initCase(ITestResult iTestResult);

    /**
     * 保存case，具体怎么实现由子类决定
     *
     * @param info          用例信息
     * @param stepEnum      步骤枚举
     * @param iTestResult   测试结果
     * @param caseCollector 收集器注解
     */
    void saveCase(Object info, CaseStepEnum stepEnum, ITestResult iTestResult, Collector caseCollector);

    /**
     * 发送通知
     */
    void sendInform(Object info, CaseStepEnum stepEnum, ITestResult iTestResult, AnnoHandle.Anno anno);
}
