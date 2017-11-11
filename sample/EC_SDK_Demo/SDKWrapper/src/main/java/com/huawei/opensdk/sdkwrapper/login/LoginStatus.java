package com.huawei.opensdk.sdkwrapper.login;


import com.huawei.tup.login.LoginAuthorizeResult;
import com.huawei.tup.login.LoginUportalAuthorizeResult;

import object.TupRegisterResult;

/**
 * This class is about login status result.
 * 登录状态结果类
 */
public class LoginStatus {

    /**
     * user id
     * 用户id
     */
    private int userID;

    /**
     * Uportal login authorize result
     * uPortal 鉴权登录结果
     */
    private LoginUportalAuthorizeResult uportalAuthorizeResult;

    /**
     * SP&IMS Hosted VC login authorize result
     * (SP&IMS Hosted VC) 鉴权登录结果
     */
    private LoginAuthorizeResult authResult;

    /**
     * Register result
     * 注册结果信息
     */
    private TupRegisterResult callResult;

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public LoginAuthorizeResult getAuthResult() {
        return authResult;
    }

    public void setAuthResult(LoginAuthorizeResult authResult) {
        this.authResult = authResult;
    }

    public TupRegisterResult getCallResult() {
        return callResult;
    }

    public void setCallResult(TupRegisterResult callResult) {
        this.callResult = callResult;
    }


    public LoginUportalAuthorizeResult getUportalAuthorizeResult() {
        return uportalAuthorizeResult;
    }

    public void setUportalAuthorizeResult(LoginUportalAuthorizeResult uportalAuthorizeResult) {
        this.uportalAuthorizeResult = uportalAuthorizeResult;
    }
}
