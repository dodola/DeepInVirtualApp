/*
 * Copyright (C) 2016 Baidu, Inc. All Rights Reserved.
 */
package dodola.binderdemo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

/**
 * Created by sunpengfei on 16/7/21.
 */

public class MyService extends Service {
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    IMyAidlInterface.Stub mBinder = new IMyAidlInterface.Stub() {
        @Override
        public String convert(String aString) throws RemoteException {
            return "=============" + aString + "=============";
        }
    };
}
