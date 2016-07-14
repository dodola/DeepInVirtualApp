/*
 * Copyright (C) 2016 Baidu, Inc. All Rights Reserved.
 */
package dodola.common.hook.base;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Lody
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Patch {
    Class<? extends Hook>[] value();
}
