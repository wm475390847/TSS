package org.sprouts.tss.conner.test.listener;

import org.sprouts.tss.conner.core.AnnoHandle;
import org.sprouts.tss.conner.enums.CaseStepEnum;
import org.sprouts.tss.conner.core.Context;
import org.sprouts.tss.conner.test.collector.ICollector;
import lombok.extern.slf4j.Slf4j;
import org.testng.*;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用于监听测试用例的执行情况
 *
 * @author wangmin18
 */
@Slf4j
public class TestListener implements ITestListener, IClassListener, ISuiteListener {

    /**
     * 用于存储方法信息，保证线程安全，在并发执行时找到执行方法的初始化用例信息
     * <p>key: 当前运行的方法 {@link ITestNGMethod}
     * <p>value: 当前用例的初始化信息 {@link Object}
     */
    private final Map<ITestNGMethod, Object> methodInfo = new ConcurrentHashMap<>(16);

    /**
     * 测试用例执行前执行
     */
    @Override
    public void onTestStart(ITestResult iTestResult) {
        System.out.println();
        logPrint(iTestResult, CaseStepEnum.START);
        initCase(iTestResult);
    }

    /**
     * 测试用例执行成功后执行
     */
    @Override
    public void onTestSuccess(ITestResult iTestResult) {
        collectCaseData(iTestResult, CaseStepEnum.SUCCESS);
    }

    /**
     * 测试用例执行失败后执行
     */
    @Override
    public void onTestFailure(ITestResult iTestResult) {
        logPrint(iTestResult, CaseStepEnum.FAILED);
        collectCaseData(iTestResult, CaseStepEnum.FAILED);
    }

    /**
     * 测试用例执行跳过后执行
     */
    @Override
    public void onTestSkipped(ITestResult iTestResult) {
        logPrint(iTestResult, CaseStepEnum.SKIPPED);
        initCase(iTestResult);
        collectCaseData(iTestResult, CaseStepEnum.SKIPPED);
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult iTestResult) {

    }

    /**
     * 用例集执行之前执行
     */
    @Override
    public void onStart(ITestContext context) {
    }

    /**
     * 用例集执行之后执行
     */
    @Override
    public void onFinish(ITestContext context) {

    }

    /**
     * 每个class之前执行
     */
    @Override
    public void onBeforeClass(ITestClass iTestClass) {
        AnnoHandle annoHandle = new AnnoHandle();
        Class<?> realClass = iTestClass.getRealClass();
        // 解析注解
        annoHandle.parse(realClass);
    }

    /**
     * 每个class之后执行
     */
    @Override
    public void onAfterClass(ITestClass iTestClass) {
    }

    @Override
    public void onStart(ISuite iSuite) {
        System.out.println();
        System.out.printf("------------------------------------------ 开始执行【%s】------------------------------------------%n", iSuite.getName());
        System.out.println();
    }

    @Override
    public void onFinish(ISuite iSuite) {
        System.out.println();
        System.out.printf("------------------------------------------ 执行结束【%s】------------------------------------------%n", iSuite.getName());
        System.out.println();
    }

    /**
     * 采集用例数据
     *
     * @param iTestResult 测试结果
     * @param stepEnum    用例步骤
     */
    public void collectCaseData(ITestResult iTestResult, CaseStepEnum stepEnum) {
        Class<?> realClass = iTestResult.getTestClass().getRealClass();
        Map<AnnoHandle.Anno, ? extends ICollector> collectorMap = Context.getCollector(realClass);
        if (collectorMap != null && !collectorMap.containsValue(null)) {
            Object info = methodInfo.get(iTestResult.getMethod());
            collectorMap.entrySet().stream().findFirst().ifPresent(map -> {
                ICollector collector = map.getValue();
                collector.saveCase(info, stepEnum, iTestResult, map.getKey().getCaseCollector());
                collector.sendInform(info, stepEnum, iTestResult, map.getKey());
            });
        }
    }

    /**
     * 初始化case信息
     *
     * @param iTestResult 测试结果
     */
    private void initCase(ITestResult iTestResult) {
        Class<?> realClass = iTestResult.getTestClass().getRealClass();
        ICollector collector = Context.getCollector(realClass)
                .entrySet().stream().findFirst().map(Map.Entry::getValue).orElse(null);
        Optional.ofNullable(collector).ifPresent(e -> methodInfo.put(iTestResult.getMethod(), e.initCase(iTestResult)));
    }

    private void logPrint(ITestResult iTestResult, CaseStepEnum stepEnum) {
        switch (stepEnum) {
            case SUCCESS:
                log.info("----------------- 成功用例【{}({})】-----------------", iTestResult.getName(), iTestResult.getMethod().getDescription());
                break;
            case FAILED:
                log.error("----------------- 失败用例【{}({})】-----------------", iTestResult.getName(), iTestResult.getMethod().getDescription());
                log.error("错误信息: {}", iTestResult.getThrowable().getMessage());
                break;
            case SKIPPED:
                log.warn("----------------- 跳过用例【{}({})】-----------------", iTestResult.getName(), iTestResult.getMethod().getDescription());
                break;
            default:
                log.info("----------------- 执行用例【{}({})】-----------------", iTestResult.getName(), iTestResult.getMethod().getDescription());
                break;
        }
    }
}
