package com.qiushangcheng.fastsqlbuilder.pathclass;

import java.lang.annotation.*;

/**
 * @auther QiuShangcheng
 * @create 2023/8/12
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface SqlBuilderPath {
    String tableName() default "";
}
