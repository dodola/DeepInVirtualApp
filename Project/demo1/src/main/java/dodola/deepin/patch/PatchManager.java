/*
 * Copyright (C) 2016 Baidu, Inc. All Rights Reserved.
 */
package dodola.deepin.patch;

import java.util.HashMap;
import java.util.Map;

import dodola.common.hook.base.IHookObject;
import dodola.common.hook.base.Injectable;
import dodola.common.hook.base.PatchObject;
import dodola.deepin.demo1.DodoApplication;
import dodola.deepin.hook.HCallbackHook;

/**
 * @author Lody
 *         注入管理器,维护全部的注入对象.
 */
public final class PatchManager {

    private static final String TAG = PatchManager.class.getSimpleName();
    private Map<Class<?>, Injectable> injectableMap = new HashMap<Class<?>, Injectable>(12);

    private PatchManager() {
    }

    /**
     * @return 注入管理器实例
     */
    public static PatchManager getInstance() {
        return PatchManagerHolder.sPatchManager;
    }

    public void checkEnv() throws Throwable {
        for (Injectable injectable : injectableMap.values()) {
            if (injectable.isEnvBad()) {
                injectable.inject();
            }
        }
    }

    /**
     * @return 是否已经初始化
     */
    public boolean isInit() {
        return PatchManagerHolder.sInit;
    }

    /**
     * 初始化PatchManager
     * <h1>必须确保只调用一次.</h1>
     */
    public void injectAll() throws Throwable {
        if (PatchManagerHolder.sInit) {
            throw new IllegalStateException("PatchManager Has been initialized.");
        }
        injectInternal();
        PatchManagerHolder.sInit = true;

    }

    private void injectInternal() throws Throwable {

        addPatch(new ActivityManagerPatch());
        if (DodoApplication.isAppClient()) {
            addPatch(HCallbackHook.getDefault());//墙裂注意此处,必须在插件进程中启动,也就是为什么插件的Activity必须是一个独立的进程而不能和宿主在同一个进程
        }


    }

    private void addPatch(Injectable injectable) {
        injectableMap.put(injectable.getClass(), injectable);
    }

    public <T extends Injectable> T findPatch(Class<T> clazz) {
        return (T) injectableMap.get(clazz);
    }


    public <T extends Injectable, H extends IHookObject> H getHookObject(Class<T> patchClass) {
        T patch = findPatch(patchClass);
        if (patch != null && patch instanceof PatchObject) {
            // noinspection unchecked
            return (H) ((PatchObject) patch).getHookObject();
        }
        return null;
    }


    private static final class PatchManagerHolder {
        private static PatchManager sPatchManager = new PatchManager();
        private static boolean sInit;
    }

}