/*
 * Copyright (C) 2016 Baidu, Inc. All Rights Reserved.
 */
package dodola.deepin.hook;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.IBinder;

import java.lang.reflect.Method;

import dodola.common.hook.base.Hook;
import dodola.common.utils.ArrayIndex;
import dodola.common.utils.ExtraConstants;
import dodola.deepin.demo1.DodoApplication;
import dodola.deepin.patch.ActivityManagerPatch;

/**
 * @author Lody
 */
/* package */public class Hook_StartActivity extends Hook<ActivityManagerPatch> {

    /**
     * 这个构造器必须有,用于依赖注入.
     *
     * @param patchObject 注入对象
     */
    public Hook_StartActivity(ActivityManagerPatch patchObject) {
        super(patchObject);
    }

    @Override
    public String getName() {
        return "startActivity";
    }

    @Override
    public Object onHook(Object who, Method method, Object... args) throws Throwable {
        int intentIndex = ArrayIndex.indexOfFirst(args, Intent.class);
        int resultToIndex;
        if (Build.VERSION.SDK_INT <= 15) {
            resultToIndex = 5;
        } else if (Build.VERSION.SDK_INT <= 17) {
            resultToIndex = 3;
        } else if (Build.VERSION.SDK_INT <= 19) {
            resultToIndex = 4;
        } else {
            resultToIndex = 4;
        }
        IBinder resultTo = (IBinder) args[resultToIndex];
        replaceIntent(resultTo, args, intentIndex);
        return method.invoke(who, args);
    }


    private void replaceIntent(IBinder resultTo, Object[] args, int intentIndex) {
        //由于我们没有在Manifest里注册这个类,所以我们获取不到这个ActivityInfo,so我们直接从一个桩里去信息赋值到启动的Activity中
        final Intent targetIntent = (Intent) args[intentIndex];

        ActivityInfo stubInfo = DodoApplication.info;
        Intent stubIntent = new Intent();
        stubIntent.setClassName(stubInfo.packageName, stubInfo.name);//此处其实new了一个ComponentName
        stubIntent.putExtra(ExtraConstants.EXTRA_TARGET_INTENT, targetIntent);//将我们要跳转的东东保存起来
        args[intentIndex] = stubIntent;
    }

}
