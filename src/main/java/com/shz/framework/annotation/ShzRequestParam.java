package com.shz.framework.annotation;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ShzRequestParam {
    String value() default  "";
    boolean required() default  true;
}
