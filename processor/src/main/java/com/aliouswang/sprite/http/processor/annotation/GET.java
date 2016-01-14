package com.aliouswang.sprite.http.processor.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by aliouswang on 16/1/14.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.CLASS)
public @interface GET {
    String value();
}
