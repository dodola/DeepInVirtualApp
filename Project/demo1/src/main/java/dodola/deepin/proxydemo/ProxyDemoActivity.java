/*
 * Copyright (C) 2016 Baidu, Inc. All Rights Reserved.
 */
package dodola.deepin.proxydemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.lang.reflect.Proxy;

import dodola.deepin.demo1.R;

public class ProxyDemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proxy_demo);
        IInterface iInterface = new InterfaceImpl();

        IInterface iInterfaceProxyObj = (IInterface) Proxy.newProxyInstance(iInterface.getClass().getClassLoader(),
                iInterface.getClass().getInterfaces(),
                new InterfaceHandler(iInterface));

        String proxyResult = iInterfaceProxyObj.getResult("Hello");

        String originResult = iInterface.getResult("hello");
        Toast.makeText(this, originResult, Toast.LENGTH_SHORT).show();


    }
}
