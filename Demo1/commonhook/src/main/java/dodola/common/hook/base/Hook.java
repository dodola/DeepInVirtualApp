/*
 * Copyright (C) 2016 Baidu, Inc. All Rights Reserved.
 */
package dodola.common.hook.base;

import java.lang.reflect.Method;

/**
 * @author Lody
 */
public abstract class Hook<T extends PatchObject> {

    private T patchObject;

    private boolean enable = true;

    /**
     * 这个构造器必须有,用于依赖注入.
     *
     * @param patchObject 注入对象
     */
    public Hook(T patchObject) {
        this.patchObject = patchObject;
    }

    /**
     * @return Hook的方法名
     */
    public abstract String getName();

    /**
     * Hook回调
     */
    public abstract Object onHook(Object who, Method method, Object... args) throws Throwable;

    public PatchObject getPatchObject() {
        return patchObject;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

}
