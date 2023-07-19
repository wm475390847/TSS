package com.chaohu.conner.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author wangmin
 * @date 2023/7/7 11:08
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DingSend {

    /**
     * 是否发送
     * 默认不发送
     *
     * @return true or false
     */
    boolean send() default true;

    /**
     * 通知人员电话
     *
     * @return 电话
     */
    String[] phones() default {};

    /**
     * 通知人员名称
     *
     * @return 名称
     */
    String[] names() default {};

}
