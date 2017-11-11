package com.huawei.opensdk.demoservice;

import com.huawei.opensdk.sdkwrapper.manager.TupMgr;
import com.huawei.tup.confctrl.sdk.TupConfManager;


public class ConfHelper
{
    private TupConfManager mTupConfManager;
    private static ConfHelper mInstance = new ConfHelper();


    private ConfHelper()
    {
        mTupConfManager = TupMgr.getInstance().getConfManagerIns();
    }

    public static ConfHelper getInstance()
    {
        return mInstance;
    }


}
