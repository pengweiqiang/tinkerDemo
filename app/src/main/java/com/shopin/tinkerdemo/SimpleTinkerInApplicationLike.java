package com.shopin.tinkerdemo;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.tencent.tinker.anno.DefaultLifeCycle;
import com.tencent.tinker.lib.tinker.TinkerInstaller;
import com.tencent.tinker.loader.app.ApplicationLike;
import com.tencent.tinker.loader.shareutil.ShareConstants;

/**
 * @author will on 2017/2/13 19:14
 * @email pengweiqiang64@163.com
 * @description Tinker的初始化工作，
 *
 * http://blog.csdn.net/lmj623565791/article/details/54882693
 * @Version
 */

@DefaultLifeCycle(application = ".SimpleTinkerInApplication",flags = ShareConstants.TINKER_ENABLE_ALL,loadVerifyFlag = false)
public class SimpleTinkerInApplicationLike extends ApplicationLike{

    public SimpleTinkerInApplicationLike(Application application, int tinkerFlags, boolean tinkerLoadVerifyFlag, long applicationStartElapsedTime, long applicationStartMillisTime, Intent tinkerResultIntent) {
        super(application, tinkerFlags, tinkerLoadVerifyFlag, applicationStartElapsedTime, applicationStartMillisTime, tinkerResultIntent);
    }

    @Override
    public void onBaseContextAttached(Context base) {
        super.onBaseContextAttached(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        TinkerInstaller.install(this);
    }
}
