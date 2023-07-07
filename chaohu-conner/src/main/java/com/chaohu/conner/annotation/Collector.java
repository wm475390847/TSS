package com.chaohu.conner.annotation;

import com.chaohu.conner.AbstractCollector;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用例收集组件
 *
 * @author wangmin
 * @date 2022/8/18 17:31
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Collector {

    Class<? extends AbstractCollector> value() default AbstractCollector.class;

    /**
     * 是否发送通知
     *
     * @return true or false
     */
    boolean sendInform() default true;

    /**
     * 是否保存用例数据
     *
     * @return true or false
     */
    boolean saveInfo() default true;
}
