package com.miracles.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by lxw
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface VersionUpgrade {

    int fromVersion();

    int toVersion();

    int priority() default 0;
}
