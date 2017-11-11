package com.huawei.opensdk.callmgr.ctdservice;

import android.util.Log;

import com.huawei.opensdk.commonservice.util.LogUtil;
import com.huawei.opensdk.sdkwrapper.login.LoginCenter;
import com.huawei.tup.ctd.CtdCallParam;
import com.huawei.tup.ctd.CtdStartCall;
import com.huawei.tup.ctd.sdk.TupCtd;
import com.huawei.tup.ctd.sdk.TupCtdNotify;
import com.huawei.tup.ctd.sdk.TupCtdOptResult;

/**
 * This class is about Ctd manager.
 * CTD模块功能管理类
 */
public class CtdMgr implements ICtdMgr, TupCtdNotify {
    private static final String TAG = CtdMgr.class.getSimpleName();

    /**
     * CTD Object Management
     * CtdMgr对象
     */
    private static CtdMgr ctdMgr;

    /**
     * CTD event notification
     * CTD回调事件对象
     */
    private ICtdNotification ctdNotification;

    /**
     * This is a constructor of CtdMgr class.
     * 构造方法
     */
    private CtdMgr()
    {

    }

    /**
     * This method is used to get CTD Object Management instances.
     * 获取ImMgr对象实例
     * @return CtdMgr Return instance object of CtdMgr
     *                返回一个CtdMgr对象实例
     */
    public static synchronized CtdMgr getInstance()
    {
        if (null == ctdMgr)
        {
            ctdMgr = new CtdMgr();
        }
        return ctdMgr;
    }

    /**
     * This method is used to register ctd module UI callback.
     * 注册回调
     * @param ctdNotification CTD event notification
     *                        CTD事件处理对象
     */
    public void regCtdNotification(ICtdNotification ctdNotification) {
        this.ctdNotification = ctdNotification;
    }

    /**
     * This method is used to start a ctd call.
     * 发起一路CTD呼叫
     * @param calleeNumber the callee number
     *                     被叫号码
     * @param callerNumber the caller number
     *                     主叫号码
     * @return result If success return 0, otherwise return corresponding error code.
     *                成功返回0，失败返回相应的错误码
     */
    public int makeCtdCall(String calleeNumber, String callerNumber)
    {
        LogUtil.e(TAG, "make a ctd call.");
        CtdCallParam ctdCallParam = new CtdCallParam();
        ctdCallParam.setToken(LoginCenter.getInstance().getCtdCfgInfo().getToken());
        ctdCallParam.setCallee_number(calleeNumber);
        ctdCallParam.setCaller_number(callerNumber);

        TupCtd tupCtd = new TupCtd();
        CtdStartCall.Response response = tupCtd.startCtdCall(ctdCallParam);
        if (response.result != 0)
        {
            LogUtil.e(TAG, "start ctd call failed, return -->" + response.result);
        }

        return response.result;
    }

    /**
     * This method is used to get start ctd call result.
     * 发起CTD呼叫结果事件
     * @param tupCtdOptResult Indicates start ctd call operation result
     *                        发起呼叫结果
     */
    @Override
    public void onStartCallResult(TupCtdOptResult tupCtdOptResult) {
        int result = tupCtdOptResult.getOptResult();
        if (result == 0)
        {
            this.ctdNotification.onStartCtdCallResult(tupCtdOptResult.getOptResult(), tupCtdOptResult.getDescription());
        }
        else
        {
            Log.e(TAG, "Start ctd call failed, result-->" + result);
        }
    }

    /**
     * This method is used to get end ctd call result.
     * 结束CTD呼叫结果事件
     * @param tupCtdOptResult Indicates end ctd call operation result
     */
    @Override
    public void onEndCallResult(TupCtdOptResult tupCtdOptResult) {
        //The mobile side is not supported temporarily.
    }

    /**
     * This method is used to get ctd call status result.
     * CTD呼叫状态通知事件
     * @param tupCtdOptResult Indicates ctd call status alert operation result
     */
    @Override
    public void onCallStatusNotify(TupCtdOptResult tupCtdOptResult) {
        //The mobile side is not supported temporarily.
    }
}
