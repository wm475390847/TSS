package org.sprouts.tss.conner.test.collector;

import org.sprouts.tss.conner.core.AnnoHandle;
import org.sprouts.tss.conner.core.annotation.Collector;
import org.sprouts.tss.conner.enums.CaseStepEnum;
import lombok.extern.slf4j.Slf4j;
import org.testng.ITestResult;

import java.util.Arrays;

/**
 * 用例收集器基类
 * <P>这里只定义一下方法的执行方向不指定具体执行内容，子类来实现具体的方法</P>
 *
 * @author wangmin
 * @date 2022/5/27 10:01
 */
@Slf4j
public abstract class AbstractCollector implements ICollector {

    /**
     * 初始化case
     *
     * @param iTestResult 执行结果
     * @return 用例数据
     */
    public abstract Object initCase(ITestResult iTestResult);

    /**
     * 保存数据
     *
     * @param info        用例数据
     * @param stepEnum    执行步骤
     * @param iTestResult 执行结果
     * @param collector   采集器
     */
    public abstract void saveCase(Object info, CaseStepEnum stepEnum, ITestResult iTestResult, Collector collector);

    /**
     * 发送通知
     *
     * @param info 用例数据
     */
    public abstract void sendInform(Object info, CaseStepEnum stepEnum, ITestResult iTestResult, AnnoHandle.Anno anno);


    /**
     * 将异常转换为字符串
     *
     * @param iTestResult 执行结果
     * @return 错误字符串
     */
    protected String throwableToString(ITestResult iTestResult) {
        Throwable throwable = iTestResult.getThrowable();
        StackTraceElement[] stackTrace = throwable.getStackTrace();
        StringBuilder sb = new StringBuilder();
        sb.append(throwable);
        Arrays.stream(stackTrace).forEach(e -> sb.append("\n").append(e.toString()));
        return sb.toString();
    }
}
