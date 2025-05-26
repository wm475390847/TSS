package org.sprouts.tss.conner.core.annotation;

import org.sprouts.tss.conner.core.config.AbstractInformConfig;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 通知注解
 * <p>通过不同的配置类来实现不同的通知方式
 *
 * @author wangmin
 * @date 2023/7/7 11:08
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Inform {

    boolean send() default true; // 是否发送通知

    Class<? extends AbstractInformConfig>[] value(); // 配置类类型

    String message() default ""; // 自定义通知内容

}
