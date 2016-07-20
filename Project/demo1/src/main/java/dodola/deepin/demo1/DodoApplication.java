/*
 * Copyright (C) 2016 Baidu, Inc. All Rights Reserved.
 */
package dodola.deepin.demo1;

import android.app.ActivityThread;
import android.app.Application;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;

import dodola.deepin.patch.PatchManager;

/**
 * Created by sunpengfei on 16/7/12.
 */

public class DodoApplication extends Application {
    public static PackageManager mOriginPackageManager;
    public static ActivityInfo info;
    public static ActivityThread mainThread;
    private static ProcessType processType;
    private String processName;
    private String mainProcessName;

    public static boolean isAppClient() {
        return processType == ProcessType.VAppClient;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //从PackageManager里拿取全部的Activity桩
        initStub();
        mainThread = ActivityThread.currentActivityThread();
// 主进程名
        mainProcessName = base.getApplicationInfo().processName;
        // 当前进程名
        processName = mainThread.getProcessName();
        if (processName.equals(mainProcessName)) {
            processType = ProcessType.Main;
        } else if (isAppProcess(processName)) {
            processType = ProcessType.VAppClient;
        } else {
            processType = ProcessType.CHILD;
        }
        mOriginPackageManager = this.getPackageManager();
        //此处注入补丁
        try {
            PatchManager.getInstance().injectAll();
            PatchManager.getInstance().checkEnv();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

    }

    private boolean isAppProcess(String processName) {
        return "dodola.deepin.demo1:v1".equals(processName);
    }

    private void initStub() {
        PackageManager pm = this.getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = pm.getPackageInfo(this.getPackageName(),
                    PackageManager.GET_ACTIVITIES | PackageManager.GET_PROVIDERS | PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (packageInfo == null) {
            throw new RuntimeException("Unable to found PackageInfo : " + this.getPackageName());
        }

        ActivityInfo[] activityInfos = packageInfo.activities;
        for (ActivityInfo activityInfo : activityInfos) {
            if (isStubComponent(activityInfo)) {
                info = activityInfo;//只写一个桩
                break;
            }
        }
    }

    private boolean isStubComponent(ComponentInfo componentInfo) {
        Bundle metaData = componentInfo.metaData;
        return metaData != null
                && TextUtils.equals(metaData.getString("dodometa"), "dododola");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static Object mainThread() {
        return null;
    }


    /**
     * 进程类型
     */
    enum ProcessType {
        /**
         * 服务端进程
         */
        Server,
        /**
         * 插件客户端进程
         */
        VAppClient,
        /**
         * 主进程
         */
        Main,
        /**
         * 子进程
         */
        CHILD
    }

}
