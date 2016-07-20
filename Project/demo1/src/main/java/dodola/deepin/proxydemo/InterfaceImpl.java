/*
 * Copyright (C) 2016 Baidu, Inc. All Rights Reserved.
 */
package dodola.deepin.proxydemo;

/**
 * Created by sunpengfei on 16/7/20.
 */

public class InterfaceImpl implements IInterface {
    @Override
    public String getResult(String oriStr) {
        return "OriStr:" + oriStr;
    }
}
