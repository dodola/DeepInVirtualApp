/*
 * Copyright (C) 2016 Baidu, Inc. All Rights Reserved.
 */
package dodola.deepin.patch;

import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.content.Context;
import android.os.IBinder;
import android.os.ServiceManager;
import android.util.Singleton;

import java.lang.reflect.Field;

import dodola.common.hook.base.HookBinder;
import dodola.common.hook.base.HookObject;
import dodola.common.hook.base.Patch;
import dodola.common.hook.base.PatchObject;
import dodola.deepin.hook.Hook_StartActivity;
import dodola.deepin.hook.Hook_StartActivityAsCaller;
import dodola.deepin.hook.Hook_StartActivityAsUser;

/**
 * @author Lody
 * @see ActivityManagerNative
 * @see IActivityManager
 * @see android.app.ActivityManager
 */

@Patch({/*Hook_StartActivities.class,*/ Hook_StartActivity.class, Hook_StartActivityAsCaller.class,
        Hook_StartActivityAsUser.class, }) public class ActivityManagerPatch
        extends PatchObject<IActivityManager, HookObject<IActivityManager>> {

    public static IActivityManager getAMN() {
        return ActivityManagerNative.getDefault();
    }

    @Override protected HookObject<IActivityManager> initHookObject() {
        return new HookObject<IActivityManager>(getAMN());
    }

    @Override public void inject() throws Throwable {

        Field f_gDefault = ActivityManagerNative.class.getDeclaredField("gDefault");
        if (!f_gDefault.isAccessible()) {
            f_gDefault.setAccessible(true);
        }
        if (f_gDefault.getType() == IActivityManager.class) {
            f_gDefault.set(null, getHookObject().getProxyObject());

        } else if (f_gDefault.getType() == Singleton.class) {

            Singleton gDefault = (Singleton) f_gDefault.get(null);
            Field f_mInstance = Singleton.class.getDeclaredField("mInstance");
            if (!f_mInstance.isAccessible()) {
                f_mInstance.setAccessible(true);
            }
            f_mInstance.set(gDefault, getHookObject().getProxyObject());
        } else {
            // 不会经过这里
            throw new UnsupportedOperationException("Singleton is not visible in AMN.");
        }

        HookBinder<IActivityManager> hookAMBinder = new HookBinder<IActivityManager>() {
            @Override protected IBinder queryBaseBinder() {
                return ServiceManager.getService(Context.ACTIVITY_SERVICE);
            }

            @Override protected IActivityManager createInterface(IBinder baseBinder) {
                return getHookObject().getProxyObject();
            }
        };
        hookAMBinder.injectService(Context.ACTIVITY_SERVICE);
    }

    @Override public boolean isEnvBad() {
        return getAMN() != getHookObject().getProxyObject();
    }
}
