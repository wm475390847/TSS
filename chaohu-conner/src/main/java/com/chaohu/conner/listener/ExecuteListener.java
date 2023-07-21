package com.chaohu.conner.listener;

import com.chaohu.conner.AbstractCollector;
import com.chaohu.conner.Context;
import com.chaohu.conner.Harbor;
import com.chaohu.conner.annotation.Collector;
import com.chaohu.conner.annotation.Container;
import com.chaohu.conner.annotation.DingSend;
import com.chaohu.conner.config.DingDingConfig;
import com.chaohu.conner.container.IConfigContainer;
import com.chaohu.conner.exception.ListenerException;
import com.chaohu.conner.util.Property;
import lombok.extern.slf4j.Slf4j;
import org.testng.*;

import java.lang.reflect.Method;

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
        AbstractCollector collector = Context.getCollector();
        if (collector != null) {
            info = collector.initInfo(iTestResult);
        }
    }

    /**
     * 用例成功后执行
     *
     * @param iTestResult iTestResult
     */
    @Override
    public void onTestSuccess(ITestResult iTestResult) {
        saveInfoAndSendInform(iTestResult, false, null);
    }

    /**
     * 用例失败后执行
     *
     * @param iTestResult iTestResult
     */
    @Override
    public void onTestFailure(ITestResult iTestResult) {
        Harbor harbor = Context.getHarbor();
        if (harbor == null) {
            return;
        }
        Method method = iTestResult.getMethod().getConstructorOrMethod().getMethod();
        DingDingConfig config = Context.getConfig(DingDingConfig.class);
        DingDingConfig newConfig = new DingDingConfig();
        if (config != null) {
            newConfig = (DingDingConfig) config.clone();
        }
        if (method.isAnnotationPresent(DingSend.class)) {
            // 如果存在方法注解则获取注解信息进行通知
            DingSend annotation = method.getAnnotation(DingSend.class);
            String[] phones = annotation.phones();
            newConfig.phones(phones);
            // 方法注解允许则发送通知，不允许则不发送通知
            if (annotation.send()) {
                saveInfoAndSendInform(iTestResult, true, newConfig);
            }
        } else {
            // 如果不存在方法注解，则按照类注解允许发送进行通知
            saveInfoAndSendInform(iTestResult, true, newConfig);
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {

    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {

    }

    @Override
    public void onStart(ITestContext iTestContext) {
        System.out.println();
        System.out.println("------------------------------------------开始执行------------------------------------------");
        Context.clearMethod();
        Context.clearClass();
    }

    @Override
    public void onFinish(ITestContext iTestContext) {
        System.out.println();
        System.out.println("------------------------------------------执行结束------------------------------------------");
        Context.clearMethod();
        Context.clearClass();
    }

    /**
     * 测试类执行之前获取注解信息存入缓存
     *
     * @param testClass 测试类
     */
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
                        .setSend(annotation.sendInform())
                        .setSave(annotation.saveInfo());
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
        // 将当前执行类与初始化数据放入内存
        Context.currentExecuteClass = realClass;
        Context.map.put(realClass, harbor);
    }

    /**
     * 测试类执行之后清理缓存
     *
     * @param testClass 测试类
     */
    @Override
    public void onAfterClass(ITestClass testClass) {
        Context.clearClass();
    }

    /**
     * 保存数据&发送数据
     *
     * @param iTestResult 测试结果
     * @param sendInform  是否发送数据
     */
    private void saveInfoAndSendInform(ITestResult iTestResult, boolean sendInform, DingDingConfig config) {
        Harbor harbor = Context.getHarbor();
        if (harbor != null) {
            AbstractCollector collector = Context.getCollector();
            if (collector != null) {
                if (harbor.getSave()) {
                    collector.saveInfo(info, iTestResult);
                }
                if (harbor.getSend() && sendInform) {
                    collector.sendInform(info, iTestResult, config);
                }
            }
        }
        Context.clearMethod();
    }
}
