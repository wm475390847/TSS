package com.chaohu.conner.annotation;

import com.chaohu.conner.container.IConfigContainer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用例配置组件
 *
 * @author wangmin
 * @date 2022/8/18 17:31
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Container {

    Class<? extends IConfigContainer> value() default IConfigContainer.class;
}

