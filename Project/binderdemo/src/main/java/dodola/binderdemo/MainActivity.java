/*
 * Copyright (C) 2016 Baidu, Inc. All Rights Reserved.
 */
package dodola.binderdemo;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends Activity {
    private IMyAidlInterface iMyAidlInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, MyService.class);
        intent.setAction(MyService.class.getName());
        this.bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                iMyAidlInterface = IMyAidlInterface.Stub.asInterface(iBinder);
                IMyAidlInterface o =
                        (IMyAidlInterface) Proxy.newProxyInstance(IMyAidlInterface.class.getClassLoader(),
                                IMyAidlInterface.Stub.class.getInterfaces(), new InvocationHandler() {
                                    @Override
                                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                                        return "hellllllllllllo";
                                    }
                                });
                try {
                    Toast.makeText(MainActivity.this, ((IMyAidlInterface) o).convert("hello"), Toast.LENGTH_SHORT)
                            .show();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                iMyAidlInterface = null;
                Log.e(MainActivity.class.getName(), "Service has unexpectedly disconnected");

            }
        }, Context.BIND_AUTO_CREATE);
    }
}
