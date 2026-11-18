package com.nbk.smart5c.common.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author jiaxuebin@nuobikan.com
 * @Desc 操作日志注解
 * @since 2024-07-26 09:47:41
 */
@Retention(RetentionPolicy.RUNTIME)//运行时有效
@Target(ElementType.METHOD)//作用在方法上
public @interface BeforeLog {
}
