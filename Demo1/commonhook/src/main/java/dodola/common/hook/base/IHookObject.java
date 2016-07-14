/*
 * Copyright (C) 2016 Baidu, Inc. All Rights Reserved.
 */
package dodola.common.hook.base;

/**
 * @author Lody
 */
public interface IHookObject<T> {

    void addHook(Hook hook);

    Hook removeHook(String hookName);

    void removeHook(Hook hook);

    void removeAllHook();

    <H extends Hook> H getHook(String name);

    T getProxyObject();

    T getBaseObject();

    int getHookCount();

}
