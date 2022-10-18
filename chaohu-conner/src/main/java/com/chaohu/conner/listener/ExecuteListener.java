package com.chaohu.conner.listener;

import com.chaohu.conner.annotation.Collector;
import com.chaohu.conner.annotation.Container;
import com.chaohu.conner.container.IConfigContainer;
import com.chaohu.conner.Context;
import com.chaohu.conner.Harbor;
import com.chaohu.conner.AbstractCollector;
import com.chaohu.conner.exception.ListenerException;
import com.chaohu.conner.util.Property;
import lombok.extern.slf4j.Slf4j;
import org.testng.*;

/**
 * 执行监听器
 *
 * @author wangmin
 * @date 2022/1/24 5:38 下午
 */
@Slf4j
public class ExecuteListener implements ITestListener, IClassListener {
    private Object info;

    /**
     * 方法执行之前执行
     *
     * @param iTestResult iTestResult
     */
    @Override
    public void onTestStart(ITestResult iTestResult) {
        Class<?> realClass = iTestResult.getTestClass().getRealClass();
        AbstractCollector collector = Context.getCollector(realClass);
        if (collector == null) {
            return;
        }
        info = collector.initInfo(iTestResult);
    }

    /**
     * 用例成功后执行
     *
     * @param iTestResult iTestResult
     */
    @Override
    public void onTestSuccess(ITestResult iTestResult) {
        saveInfoAndSendInform(iTestResult, false);
    }

    /**
     * 用例失败后执行
     *
     * @param iTestResult iTestResult
     */
    @Override
    public void onTestFailure(ITestResult iTestResult) {
        saveInfoAndSendInform(iTestResult, true);
    }

    @Override
    public void onTestSkipped(ITestResult iTestResult) {

    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult iTestResult) {

    }

    @Override
    public void onStart(ITestContext iTestContext) {
        System.out.println("------------------------------------------开始执行------------------------------------------");
        cleanClass();
    }

    @Override
    public void onFinish(ITestContext iTestContext) {
        cleanClass();
        System.out.println();
        System.out.println("------------------------------------------执行结束------------------------------------------");
    }

    private void cleanClass() {
        Context.detailMessage = null;
        Context.failApi = null;
    }

    @Override
    public void onBeforeClass(ITestClass testClass) {
        Class<?> realClass = testClass.getRealClass();
        Harbor harbor = new Harbor();
        boolean collectAnnotation = realClass.isAnnotationPresent(Collector.class);
        if (collectAnnotation) {
            Collector annotation = realClass.getAnnotation(Collector.class);
            Class<? extends AbstractCollector> value = annotation.value();
            try {
                AbstractCollector abstractCollector = value.newInstance();
                harbor.setAbstractCollector(abstractCollector)
                        .setSendInfo(annotation.sendInform())
                        .setSaveInfo(annotation.saveInfo());
            } catch (InstantiationException | IllegalAccessException e) {
                throw new ListenerException(e.getMessage());
            }
        }
        boolean containerAnnotation = realClass.isAnnotationPresent(Container.class);
        if (containerAnnotation) {
            Class<? extends IConfigContainer> value = realClass.getAnnotation(Container.class).value();
            try {
                IConfigContainer configContainer = value.newInstance();
                configContainer.setProperties(Property.getInstance().parse());
                configContainer.init();
                harbor.setConfigContainer(configContainer);
            } catch (InstantiationException | IllegalAccessException e) {
                throw new ListenerException(e.getMessage());
            }
        }
        Context.map.put(realClass, harbor);
    }

    @Override
    public void onAfterClass(ITestClass testClass) {
        Context.currentExecuteClass = null;
    }

    /**
     * 保存数据&发送数据
     *
     * @param iTestResult 测试结果
     * @param sendInfo    是否发送数据
     */
    private void saveInfoAndSendInform(ITestResult iTestResult, boolean sendInfo) {
        Class<?> realClass = iTestResult.getTestClass().getRealClass();
        Harbor harbor = Context.getHarbor(realClass);
        AbstractCollector collector = Context.getCollector(realClass);
        if (harbor == null || collector == null) {
            cleanClass();
            return;
        }
        if (harbor.getSaveInfo()) {
            collector.saveInfo(info, iTestResult);
        }
        if (harbor.getSendInfo() && sendInfo) {
            collector.sendInform(info, iTestResult.getThrowable());
        }
        cleanClass();
    }
}
