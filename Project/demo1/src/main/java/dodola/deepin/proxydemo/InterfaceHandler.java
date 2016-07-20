/*
 * Copyright (C) 2016 Baidu, Inc. All Rights Reserved.
 */
package dodola.deepin.proxydemo;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by sunpengfei on 16/7/20.
 */

public class InterfaceHandler implements InvocationHandler {
    private IInterface target;

    public InterfaceHandler(IInterface _target) {
        target = _target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ("getResult".equals(method.getName())) {
            args[0] += "=====after";
            return "before=====" + method.invoke(target, args);
        }
        return null;
    }
}
