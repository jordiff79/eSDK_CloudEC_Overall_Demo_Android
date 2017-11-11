package com.huawei.opensdk.loginmgr;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.huawei.opensdk.commonservice.util.DeviceManager;
import com.huawei.opensdk.commonservice.util.LogUtil;
import com.huawei.opensdk.imservice.ImMgr;
import com.huawei.opensdk.sdkwrapper.login.ITupLoginCenterNotify;
import com.huawei.opensdk.sdkwrapper.login.ImAccountInfo;
import com.huawei.opensdk.sdkwrapper.login.LoginCenter;
import com.huawei.opensdk.sdkwrapper.login.LoginEvent;
import com.huawei.opensdk.sdkwrapper.login.LoginResult;
import com.huawei.opensdk.sdkwrapper.login.LoginStatus;
import com.huawei.opensdk.sdkwrapper.login.SipAccountInfo;
import com.huawei.opensdk.sdkwrapper.manager.TupMgr;
import com.huawei.tup.login.LoginAuthInfo;
import com.huawei.tup.login.LoginAuthServerInfo;
import com.huawei.tup.login.LoginAuthType;
import com.huawei.tup.login.LoginAuthorizeParam;
import com.huawei.tup.login.LoginServerType;

/**
 *  This class is about login manager
 *  登录管理类
 */
public class LoginMgr implements ITupLoginCenterNotify
{
    private static final String TAG = LoginMgr.class.getSimpleName();

    /**
     * Login manager instance
     * 登录管理实例
     */
    private static LoginMgr instance;

    /**
     * UI callback
     * UI回调
     */
    private ILoginEventNotifyUI loginEventNotifyUI;

    /**
     * Force exit
     * 强制退出
     */
    private boolean isForceLogout = false;

    /**
     * This method is used to get login manager instance
     * 获取一个登录管理类的实例对象
     * @return LoginMgr : login manager instance
     *                      返回一个登录管理类实例对象
     */
    public static LoginMgr getInstance()
    {
        if (null == instance)
        {
            instance = new LoginMgr();
        }
        return instance;
    }

    /**
     * This method is used to registered login callback
     * 注册UI回调函数
     * @param notify : UI callback
     */
    public void regLoginEventNotification(ILoginEventNotifyUI notify)
    {
        this.loginEventNotifyUI = notify;
    }

    /**
     * This method is used to login
     * 鉴权登录
     * @param loginParam : login param 登录入参
     * @return int : 0:success
     */
    public int login(LoginParam loginParam)
    {
        //Get local IP
        //获取本地IP
        String localIpAddress = DeviceManager.getLocalIpAddress(loginParam.isVPN());
        LoginCenter.getInstance().setLocalIPAddress(localIpAddress);

        //sip authorize info
        //sip鉴权信息
        LoginAuthInfo authInfo = new LoginAuthInfo();
        authInfo.setUserName(loginParam.getUserName());
        authInfo.setPassword(loginParam.getPassword());

        //login auth server info
        //鉴权登录服务器信息
        LoginAuthServerInfo serverInfo = new LoginAuthServerInfo();
        serverInfo.setServerType(LoginServerType.LOGIN_E_SERVER_TYPE_PORTAL);
        serverInfo.setServerUrl(loginParam.getServerUrl());
        serverInfo.setServerPort(loginParam.getServerPort());
        serverInfo.setServerVersion("");
        serverInfo.setProxyUrl(loginParam.getProxyUrl());
        serverInfo.setProxyPort(loginParam.getProxyPort());

        //login authorize param
        //鉴权登录入参
        LoginAuthorizeParam authorizeParam = new LoginAuthorizeParam();
        authorizeParam.setUserId(1);
        authorizeParam.setAuthType(LoginAuthType.LOGIN_E_AUTH_NORMAL);
        authorizeParam.setAuthInfo(authInfo);
        authorizeParam.setAuthServer(serverInfo);
        authorizeParam.setUserAgent("SoftClient on Mobile");
        authorizeParam.setUserTiket("");

        int uportalLoginResult = LoginCenter.getInstance().login(authorizeParam);
        if (uportalLoginResult != 0)
        {
            LogUtil.e(TAG, "authorize is failed, return " + uportalLoginResult);
            return uportalLoginResult;
        }
        LogUtil.i(TAG, "start authorize.");
        return uportalLoginResult;
    }


    /**
     * This method is used to logout
     * 登出
     */
    public void logout()
    {
        LoginCenter.getInstance().logout();
        ImMgr.getInstance().imLogout();
    }


    @Override
    public void onLoginEventNotify(LoginEvent evt, LoginResult result, LoginStatus status)
    {

        switch (evt)
        {
            //Authentication login failed
            case LOGIN_E_EVT_AUTH_FAILED:
                LogUtil.e(TAG, "authorize failed: " + result.getDescription());
                this.loginEventNotifyUI.onLoginEventNotify(LoginConstant.LoginUIEvent.LOGIN_FAILED, result.getReason(), result.getDescription());
                break;

            //Authentication login success
            case LOGIN_E_EVT_AUTH_SUCCESS:
                LogUtil.e(TAG, "authorize success.");

                // 启动IM的登录
                if (TupMgr.getInstance().getFeatureMgr().isSupportIM()) {
                    sendHandlerMessage(evt.getIndex(), LoginCenter.getInstance().getImAccountInfo());
                }
                break;

            //Voip login success
            case LOGIN_E_EVT_VOIP_LOGIN_SUCCESS:
                LogUtil.i(TAG, "voip login success");
                this.loginEventNotifyUI.onLoginEventNotify(LoginConstant.LoginUIEvent.LOGIN_SUCCESS, result.getReason(), result.getDescription());

                break;

            //Voip login failed
            case LOGIN_E_EVT_VOIP_LOGIN_FAILED:
                LogUtil.e(TAG, "voip login failed: " + result.getDescription());
                this.loginEventNotifyUI.onLoginEventNotify(LoginConstant.LoginUIEvent.LOGIN_FAILED, result.getReason(), result.getDescription());
                break;

            //Voip logout success
            case LOGIN_E_EVT_VOIP_LOGOUT_SUCCESS:
                LogUtil.i(TAG, "voip logout success");
                if (isForceLogout == false)
                {
                    this.loginEventNotifyUI.onLoginEventNotify(LoginConstant.LoginUIEvent.LOGOUT, result.getReason(), result.getDescription());
                }
                isForceLogout = false;
                break;

            //Voip logout failed
            case LOGIN_E_EVT_VOIP_LOGOUT_FAILED:
                break;

            //Voip force logout
            case LOGIN_E_EVT_VOIP_FORCE_LOGOUT:
                LogUtil.i(TAG, "voip force logout");
                isForceLogout = true;
                this.logout();

                this.loginEventNotifyUI.onLoginEventNotify(LoginConstant.LoginUIEvent.LOGOUT, result.getReason(), result.getDescription());
                break;

            default:
                break;
        }

    }

    /**
     * This method is used to get sip number
     * 获取sip号码或者终端号
     * @return String : sip number or terminal
     *                      返回sip号码或者终端号
     */
    public String getSipNumber()
    {
        SipAccountInfo sipAccountInfo = LoginCenter.getInstance().getSipAccountInfo();
        if (sipAccountInfo != null)
        {
            return sipAccountInfo.getTerminal();
        }else{
            Log.e(TAG, "get sip number is null");
            return "null";
        }
    }

    /**
     * This method is used to get account
     * 获取用户账号
     * @return String : account number
     *                  返回用户账号
     */
    public String getAccount()
    {
        return LoginCenter.getInstance().getAccount();
    }

    private Handler mMainHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            LogUtil.i(TAG, "what:" + msg.what);
            parallelHandleMessage(msg);
        }
    };

    private void sendHandlerMessage(int what, Object object) {
        if (mMainHandler == null) {
            return;
        }
        Message msg = mMainHandler.obtainMessage(what, object);
        mMainHandler.sendMessage(msg);
    }

    private void parallelHandleMessage(Message msg) {
        if (msg.what == LoginEvent.LOGIN_E_EVT_AUTH_SUCCESS.getIndex()) {
            //IM login
            //登录IM
            ImMgr.getInstance().login((ImAccountInfo) msg.obj);
        }
    }

}
