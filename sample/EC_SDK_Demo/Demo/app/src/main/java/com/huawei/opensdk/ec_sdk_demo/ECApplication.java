package com.huawei.opensdk.ec_sdk_demo;

import android.app.Application;
import android.util.Log;

import com.huawei.opensdk.callmgr.CallMgr;
import com.huawei.opensdk.callmgr.ctdservice.CtdMgr;
import com.huawei.opensdk.commonservice.common.LocContext;
import com.huawei.opensdk.commonservice.util.LogUtil;
import com.huawei.opensdk.contactservice.eaddr.EnterpriseAddressBookMgr;
import com.huawei.opensdk.demoservice.MeetingMgr;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;
import com.huawei.opensdk.ec_sdk_demo.logic.call.CallFunc;
import com.huawei.opensdk.ec_sdk_demo.logic.conference.ConfFunc;
import com.huawei.opensdk.ec_sdk_demo.logic.eaddrbook.EnterpriseAddrBookFunc;
import com.huawei.opensdk.ec_sdk_demo.logic.im.ImFunc;
import com.huawei.opensdk.ec_sdk_demo.logic.login.LoginFunc;
import com.huawei.opensdk.imservice.ImMgr;
import com.huawei.opensdk.ec_sdk_demo.util.FileUtil;
import com.huawei.opensdk.ec_sdk_demo.util.ZipUtil;
import com.huawei.opensdk.loginmgr.LoginMgr;
import com.huawei.opensdk.servicemgr.ServiceMgr;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class ECApplication extends Application
{
    private static final int EXPECTED_FILE_LENGTH = 7;


    @Override
    public void onCreate()
    {
        super.onCreate();

        String appPath = getApplicationInfo().dataDir + "/lib";
        ServiceMgr.getServiceMgr().startService(this, appPath);
        Log.i("SDKDemo", "onCreate: ECApplication.");

        LoginMgr.getInstance().regLoginEventNotification(LoginFunc.getInstance());
        CallMgr.getInstance().regCallServiceNotification(CallFunc.getInstance());
        CtdMgr.getInstance().regCtdNotification(CallFunc.getInstance());
        MeetingMgr.getInstance().regConfServiceNotification(ConfFunc.getInstance());
        ImMgr.getInstance().regImServiceNotification(ImFunc.getInstance());
        EnterpriseAddressBookMgr.getInstance().registerNotification(EnterpriseAddrBookFunc.getInstance());

        initResourceFile();
        //Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
    }

    private void initResourceFile()
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                initDataConfRes();
            }
        }).start();
    }

    private void initDataConfRes()
    {
        String path = LocContext.
                getContext().getFilesDir() + "/AnnoRes";
        File file = new File(path);
        if (file.exists())
        {
            LogUtil.i(UIConstants.DEMO_TAG,  file.getAbsolutePath());
            File[] files = file.listFiles();
            if (null != files && EXPECTED_FILE_LENGTH == files.length)
            {
                return;
            }
            else
            {
                FileUtil.deleteFile(file);
            }
        }

        try
        {
            InputStream inputStream = getAssets().open("AnnoRes.zip");
            ZipUtil.unZipFile(inputStream, path);
        }
        catch (IOException e)
        {
            LogUtil.i(UIConstants.DEMO_TAG,  "close...Exception->e" + e.toString());
        }
    }

    @Override
    public void onTerminate()
    {
        super.onTerminate();

        ServiceMgr.getServiceMgr().stopService();
    }
}
