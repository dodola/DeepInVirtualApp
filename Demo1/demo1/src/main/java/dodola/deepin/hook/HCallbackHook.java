/*
 * Copyright (C) 2016 Baidu, Inc. All Rights Reserved.
 */
package dodola.deepin.hook;

import android.app.ActivityThread;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import java.lang.reflect.Field;

import dodola.common.hook.base.Injectable;
import dodola.common.utils.ActivityRecordCompat;
import dodola.common.utils.ExtraConstants;
import dodola.deepin.demo1.DodoApplication;

/**
 * @author Lody
 *         <p>
 *         <p>
 *         注入我们的Callback到
 *         <h1>ActivityThread$H</h1>.
 * @see Handler.Callback
 * @see ActivityThread
 */
public class HCallbackHook implements Handler.Callback, Injectable {

    ////////////////////////////////////////////////////////////////
    ////////////////// Copy from ActivityThread$H////////////////////
    ////////////////////////////////////////////////////////////////
    public static final int LAUNCH_ACTIVITY = 100;

    private static final String TAG = HCallbackHook.class.getSimpleName();
    private static final HCallbackHook sCallback = new HCallbackHook();
    private static Field f_h;
    private static Field f_handleCallback;

    static {
        try {
            f_h = ActivityThread.class.getDeclaredField("mH");
            f_handleCallback = Handler.class.getDeclaredField("mCallback");
            f_h.setAccessible(true);
            f_handleCallback.setAccessible(true);
        } catch (NoSuchFieldException e) {
            // Ignore
        }
    }

    /**
     * 其它插件化可能也会注入Activity$H, 这里要保留其它插件化的Callback引用，我们的Callback完事后再调用它的。
     */
    private Handler.Callback otherCallback;

    private HCallbackHook() {
    }

    public static HCallbackHook getDefault() {
        return sCallback;
    }

    public static Handler getH() {
        try {
            return (Handler) f_h.get(DodoApplication.mainThread);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Handler.Callback getHCallback() {
        try {
            Handler handler = getH();

            return (Handler.Callback) f_handleCallback.get(handler);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case LAUNCH_ACTIVITY: {
                if (!handleLaunchActivity(msg)) {
                    return true;
                }
                break;
            }
        }
        if (true) {
            return false;
        }
        // 向下调用兼容其它的插件化
        return otherCallback != null && otherCallback.handleMessage(msg);
    }

    private boolean handleLaunchActivity(Message msg) {
        Object r = msg.obj;
        //Callback回来我们需要替换成我们需要启动的,因为没有注册的Activity获取不到ActivityInfo在前面验证会直接报错,
        //所以我们用桩的name让系统获取到一个可用的ActivityInfo,跳过验证,最终会回到这里,这里一定要处理否则掉起的就是桩的Activity了

        Intent stubIntent = ActivityRecordCompat.getIntent(r);

        // TargetIntent
        Intent targetIntent = stubIntent.getParcelableExtra(ExtraConstants.EXTRA_TARGET_INTENT);
        stubIntent.setComponent(targetIntent.getComponent());//换回去,你懂的

        return true;
    }

    @Override
    public void inject() throws Throwable {
        otherCallback = getHCallback();
        f_handleCallback.set(getH(), this);
    }

    @Override
    public boolean isEnvBad() {
        Handler.Callback callback = getHCallback();
        boolean envBad = callback != this;
        if (envBad) {
        }
        return envBad;
    }

}
