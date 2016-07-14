/*
 * Copyright (C) 2016 Baidu, Inc. All Rights Reserved.
 */
package dodola.common.stub;

import android.app.Activity;

/**
 * 该类作为Activity的桩
 */
public abstract class StubActivity extends Activity {

    public static class Stub1 extends StubActivity {//注意此处必须是静态的,否则会出现错误
    }
}