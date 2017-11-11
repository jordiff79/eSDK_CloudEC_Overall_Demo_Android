package com.huawei.opensdk.sdkwrapper.login;

import android.text.TextUtils;
import android.util.Log;

import com.huawei.opensdk.sdkwrapper.manager.TupMgr;
import com.huawei.tup.confctrl.ConfctrlConfEnvType;
import com.huawei.tup.eaddr.TupEaddrContactorInfo;
import com.huawei.tup.login.LoginAccessServer;
import com.huawei.tup.login.LoginAuthorizeParam;
import com.huawei.tup.login.LoginAuthorizeSiteInfo;
import com.huawei.tup.login.LoginDeployMode;
import com.huawei.tup.login.LoginDetectServer;
import com.huawei.tup.login.LoginFirewallMode;
import com.huawei.tup.login.LoginSingleServerInfo;
import com.huawei.tup.login.LoginStgParam;
import com.huawei.tup.login.LoginUportalAuthorizeResult;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import common.FireWallMode;
import object.TupCallCfgAccount;
import object.TupCallCfgSIP;

/**
 * This class is about login configuration.
 * 登陆配置相关信息类
 */
public class LoginCenter {

    private static final String TAG = LoginCenter.class.getSimpleName();

    /**
     * EC Solutions
     * EC解决方案
     */
    public static final int CLOUD_EC  = 0;

    /**
     * PBX Solutions
     * PBX解决方案
     */
    public static final int CLOUD_PBX  = 1;

    /**
     * Define a LoginCenter object
     * LoginCenter对象
     */
    private static LoginCenter instance;

    /**
     * Define a TupEaddrContactorInfo object
     * 通讯录联系人详细信息对象
     */
    private TupEaddrContactorInfo selfInfo;

    /**
     * Define a LoginStatus object
     * LoginStatus对象
     */
    private LoginStatus loginStatus;

    /**
     * Define a ImAccountInfo object
     * Im账号信息配置对象
     */
    private ImAccountInfo imAccountInfo;

    /**
     * Define a SipAccountInfo object
     * Sip账号信息对象
     */
    private SipAccountInfo sipAccountInfo;

    /**
     * Define a conference configuration information object
     * 会控配置信息对象
     */
    private ConfConfigInfo confConfigInfo;

    /**
     * Define a CTD server parameters object
     * CTD配置信息对象
     */
    private CtdConfigInfo ctdCfgInfo;

    /**
     * Define a Address book configuration information object
     * 企业地址本配置信息对象
     */
    private EntAddressBookConfigInfo entAddressBookConfigInfo;

    /**
     * Define a STG Information object
     * STG隧道参数对象
     */
    private LoginStgParam stgParam;

    /**
     * Firewall type
     * 防火墙模式
     */
    private LoginFirewallMode firewallMode = LoginFirewallMode.LOGIN_E_FIREWALL_MODE_NULL;

    /**
     * account
     * 账号
     */
    private String account;

    /**
     * Login server address
     * 服务器地址
     */
    private String loginServerAddress;

    /**
     * Login server port
     * 端口号
     */
    private int loginServerPort;

    /**
     * local IP address
     * 本地ip地址
     */
    private String localIPAddress;

    /**
     * Type of conference
     * 会议类型
     */
    private int deployMode;

    /**
     * solution : CLOUD_EC or CLOUD_PBX
     * 解决方案
     */
    private int solution;

    /**
     * This is a constructor of LoginCenter class.
     * 构造方法
     */
    private LoginCenter() {
        this.loginStatus = new LoginStatus();
    }

    /**
     * This method is used to get instance object of LoginCenter.
     * 获取LoginCenter对象实例
     * @return LoginCenter Return instance object of LoginCenter
     *                     返回一个LoginCenter对象实例
     * @return LoginCenter
     */
    public synchronized static LoginCenter getInstance() {
        if (instance == null) {
            instance = new LoginCenter();
        }
        return instance;
    }

    /**
     * This method is used to login.
     * 鉴权登陆
     * @param loginParam Indicates authorize param
     *                   鉴权参数
     * @return int If success return 0, otherwise return corresponding error code
     *             成功返回0，失败返回相应错误码
     */
    public int login(LoginAuthorizeParam loginParam) {
        int ret;

        if (loginParam == null) {
            return -1;
        }

        this.account = loginParam.getAuthInfo().getUserName();
        this.loginServerAddress = loginParam.getAuthServer().getServerUrl();
        this.loginServerPort = loginParam.getAuthServer().getServerPort();

        ret = TupMgr.getInstance().getAuthManagerIns().authorize(loginParam);
        if (ret != 0) {
            Log.e(TAG, "login is failed" + ret);
        }

        return ret;
    }

    /**
     * This method is used to logout.
     * 账号注销
     * @return int If success return 0, otherwise return corresponding error code
     *             成功返回0，失败返回相应错误码
     */
    public int logout() {
        int ret = 0;

        /* Unregister Sip */
        if (TupMgr.getInstance().getFeatureMgr().isSupportAudioAndVideoCall() == true) {
            //SIP账号注销
            ret = this.sipUnReg();
            if (ret != 0) {
                Log.e(TAG, "Sip unregister failed, return " + ret);
            }
        }

        return ret;
    }

    /**
     * This method is used to register sip account.
     * SIP账号注册
     * @param sipAccountInfo Indicates sip account info
     *                       sip账号的相关信息
     * @return int If success return 0, otherwise return corresponding error code
     *             成功返回0，失败返回相应错误码
     */
    public int sipReg(SipAccountInfo sipAccountInfo) {
        int ret;

        TupMgr.getInstance().getCallManagerIns().setTelNum(LoginCenter.getInstance().getSipAccountInfo().getSipNumber());

        /* Set server address and local address info */
        TupCallCfgSIP tupCallCfgSIP = TupMgr.getInstance().getTupCallCfgSIP();
        tupCallCfgSIP.setServerRegPrimary(sipAccountInfo.getRegisterServerAddr(), sipAccountInfo.getRegisterServerPort());
        tupCallCfgSIP.setServerProxyPrimary(sipAccountInfo.getProxyServerAddr(), sipAccountInfo.getProxyServerPort());
        tupCallCfgSIP.setNetAddress(sipAccountInfo.getLocalIpAddress());
        tupCallCfgSIP.setSipPort(sipAccountInfo.getLocalSIPPort());

        TupMgr.getInstance().getCallManagerIns().setCfgSIP(tupCallCfgSIP);

        /* Set sip password type */
        TupCallCfgAccount tupCallCfgAccount = new TupCallCfgAccount();
        tupCallCfgAccount.setauthPasswordType(sipAccountInfo.getSipAuthPasswordType());
        TupMgr.getInstance().getCallManagerIns().setCfgAccount(tupCallCfgAccount);

        /* Start register sip account */
        ret = TupMgr.getInstance().getCallManagerIns().callRegister(sipAccountInfo.getSipImpi(), sipAccountInfo.getSipImpi(), sipAccountInfo.getSipPassword());
        if (ret != 0) {
            Log.e(TAG, "Sip register failed, retrun " + ret);
        }

        return ret;
    }

    /**
     * This method is used to deregister sip account.
     * SIP账号注销
     *
     * @return int If success return 0, otherwise return corresponding error code
     *             成功返回0，失败返回相应错误码
     */
    public int sipUnReg() {
        return TupMgr.getInstance().getCallManagerIns().callDeregister();
    }

    /**
     * This method is used to set sip service firewall mode.
     * 设置防火墙模式
     *
     * @param mode Indicates the fire wall mode
     *             防火墙模式
     */
    public void setSipFirewallMode(LoginFirewallMode mode) {
        FireWallMode fireWallMode = FireWallMode.CALL_E_FIREWALL_MODE_LINE;
        switch (mode) {
            //直连模式
            case LOGIN_E_FIREWALL_MODE_NULL:
                fireWallMode = FireWallMode.CALL_E_FIREWALL_MODE_LINE;
                break;
            //内置SVN模式
            case LOGIN_E_FIREWALL_MODE_ONLY_HTTP:
                fireWallMode = FireWallMode.CALL_E_FIREWALL_MODE_INNERSVN;
                break;
            //启用STG模式
            case LOGIN_E_FIREWALL_MODE_HTTP_AND_SVN:
                fireWallMode = FireWallMode.CALL_E_FIREWALL_MODE_STG;
                break;

            default:
                break;
        }

        TupCallCfgSIP tupCallCfgSIP = TupMgr.getInstance().getTupCallCfgSIP();
        tupCallCfgSIP.setFireWallMode(fireWallMode);

        //设置SIP相关配置参数
        TupMgr.getInstance().getCallManagerIns().setCfgSIP(tupCallCfgSIP);
    }

    /**
     * This method is used to get the control configuration information.
     * 从鉴权登陆结果中获取会控的配置信息
     * @param loginUportalAuthorizeResult Indicates login authorize result
     *                                    鉴权登陆结果信息
     *
     * @return ConfConfigInfo Return the control configuration information
     *                        返回会控的配置信息
     */
    public ConfConfigInfo getConfAccountInfoFromAuthResult(LoginUportalAuthorizeResult loginUportalAuthorizeResult) {
        ConfConfigInfo confConfigInfo = new ConfConfigInfo();

        ConfctrlConfEnvType confEnvType = ConfctrlConfEnvType.CONFCTRL_E_CONF_ENV_CONVERGENT_MEETING;
        LoginDeployMode deployMode = LoginDeployMode.values()[loginUportalAuthorizeResult.getDeployMode()];
        switch (deployMode) {
            case LOGIN_E_DEPLOY_ENTERPRISE_IPT:
            case LOGIN_E_DEPLOY_SPHOSTED_IPT:
            case LOGIN_E_DEPLOY_IMSHOSTED_IPT:
                confEnvType = ConfctrlConfEnvType.CONFCTRL_E_CONF_ENV_CONVERGENT_MEETING;
                setSolution(CLOUD_PBX);
                break;

            case LOGIN_E_DEPLOY_SPHOSTED_CONF:
                confEnvType = ConfctrlConfEnvType.CONFCTRL_E_CONF_ENV_CONVERGENT_MEETING;
                setSolution(CLOUD_EC);
                break;

            case LOGIN_E_DEPLOY_ENTERPRISE_CC:
                confEnvType = ConfctrlConfEnvType.CONFCTRL_E_CONF_ENV_ON_PREMISE_CONVERGENT_MEETING;
                setSolution(CLOUD_EC);
                break;

            case LOGIN_E_DEPLOY_SPHOSTED_CC:
            case LOGIN_E_DEPLOY_IMSHOSTED_CC:
                confEnvType = ConfctrlConfEnvType.CONFCTRL_E_CONF_ENV_CONVERGENT_MEETING;
                setSolution(CLOUD_EC);
                break;

            default:
                break;
        }

        //设置会议环境类型
        confConfigInfo.setConfEnvType(confEnvType);

        //设置会议服务器地址和端口号
        LoginSingleServerInfo loginSingleServerInfo = loginUportalAuthorizeResult.getAuthSerinfo();
        if (loginSingleServerInfo != null) {
            confConfigInfo.setServerUri(loginSingleServerInfo.getServerUri());
            confConfigInfo.setServerPort(loginSingleServerInfo.getServerPort());
        }

        //设置MS会议参数获取服务器路径
        List<LoginAuthorizeSiteInfo> siteInfoList = loginUportalAuthorizeResult.getSiteInfo();
        LoginAccessServer accessServer = siteInfoList.get(0).getAccessServer().get(0);
        confConfigInfo.setMsParamPathUri(accessServer.getMsParamPathUri());

        //设置MS会议参数获取服务器地址
        String uri = accessServer.getMsParamUri();
        String[] msParamUri = uri.split(":");
        confConfigInfo.setMsParamUri(msParamUri[0]);

        //confConfigInfo.setMsUriList();

        return confConfigInfo;
    }

    /**
     * This method is used to get enterprise address book server parameters.
     * 获取企业通讯录功能配置信息参数
     * @param authorizeResult Indicates login authorize result
     *                        鉴权登陆结果信息
     * @return EntAddressBookConfigInfo Return the enterprise address book server parameters
     *                                  返回企业通讯录功能配置信息参数
     */
    public EntAddressBookConfigInfo getEaddrConfigInfo(LoginUportalAuthorizeResult authorizeResult) {
        LoginSingleServerInfo singleServerInfo = authorizeResult.getAuthSerinfo();
        EntAddressBookConfigInfo configInfo = new EntAddressBookConfigInfo();
        configInfo.setImpi(authorizeResult.getSipImpi());
        configInfo.setToken(authorizeResult.getAuthToken());
        configInfo.setReg_port(singleServerInfo.getServerPort());
        configInfo.setReg_server(singleServerInfo.getServerUri());
        return configInfo;
    }

    /**
     * This method is used to get CTD server parameters.
     * 获取CTD配置相关参数
     * @param uportalAuthorizeResult Indicates login authorize result
     *                               鉴权登陆结果信息
     * @return CtdConfigInfo Return the CTD server parameters
     *                       返回CTD配置相关参数
     */
    public CtdConfigInfo getCtdCallInfo(LoginUportalAuthorizeResult uportalAuthorizeResult) {
        CtdConfigInfo ctdCallInfo = new CtdConfigInfo();
        ctdCallInfo.setCallerNumber(uportalAuthorizeResult.getSipAccount());
        ctdCallInfo.setToken(uportalAuthorizeResult.getAuthToken());
        ctdCallInfo.setServerPort(uportalAuthorizeResult.getAuthSerinfo().getServerPort());
        ctdCallInfo.setServerAddr(uportalAuthorizeResult.getAuthSerinfo().getServerUri());
        return ctdCallInfo;
    }

    /**
     * This method is used to get im account server parameters.
     * 获取IM 账号配置相关参数
     * @param loginUportalAuthorizeResult Indicates login authorize result
     *                                    鉴权登陆结果信息
     * @return ImAccountInfo Return the im account server parameters
     *                       返回IM 账号配置相关参数
     */
    public ImAccountInfo getImAccountInfoFromAuthResult(LoginUportalAuthorizeResult loginUportalAuthorizeResult) {

        //暂未能实现容灾
        LoginAccessServer accessServer = loginUportalAuthorizeResult.getSiteInfo().get(0).getAccessServer().get(0);
        String proPhotoServer = accessServer.getProphotoUri();
        String msUri = accessServer.getMsParamUri();
        String server = "https://" + msUri;
        if (TextUtils.isEmpty(proPhotoServer))
        {
            proPhotoServer = server + "/headportrait";
        }
        String maaUri = accessServer.getMaaUri();
        String maaServer = maaUri.substring(0, maaUri.indexOf(":"));
        String maaServerPort = maaUri.substring(maaUri.indexOf(":") + 1, maaUri.length());

        ImAccountInfo imAccountInfo = new ImAccountInfo();
        imAccountInfo.setAccount(this.account);
        imAccountInfo.setToken(loginUportalAuthorizeResult.getAuthToken());
        imAccountInfo.setPassword("null");
        imAccountInfo.setMaaServer(maaServer);
        imAccountInfo.setMaaServerPort(Integer.valueOf(maaServerPort));
        imAccountInfo.setPortraitServer(proPhotoServer);

        return imAccountInfo;
    }

    /**
     * This method is used to get sip register account information.
     * 获取sip 注册时的账号信息
     * @param loginUportalAuthorizeResult Indicates login authorize result
     *                                    鉴权登陆结果信息
     * @return SipAccountInfo Return the sip register account information
     *                        返回SIP 注册时的账号信息
     */
    public SipAccountInfo getSipAccountInfoFromAuthResult(LoginUportalAuthorizeResult loginUportalAuthorizeResult) {
        //暂未能实现容灾
        String regServerAddr = "";
        String regServerPort = "5060";

        //鉴权地址信息
        List<LoginAuthorizeSiteInfo> siteInfo = loginUportalAuthorizeResult.getSiteInfo();
        Iterator siteInfoIterator = siteInfo.iterator();
        while (siteInfoIterator.hasNext()) {
            LoginAuthorizeSiteInfo loginAuthorizeSiteInfo = (LoginAuthorizeSiteInfo) siteInfoIterator.next();
            List<LoginAccessServer> accessServer = loginAuthorizeSiteInfo.getAccessServer();

            Iterator accessServerIterator = accessServer.iterator();
            while (accessServerIterator.hasNext()) {
                LoginAccessServer loginAccessServer = (LoginAccessServer) accessServerIterator.next();

                String sipuri = loginAccessServer.getSipUri();
                String[] sipUri = sipuri.split(":");
                regServerAddr = sipUri[0];
                regServerPort = sipUri[1];
            }
        }

        //SIP注册的账号信息
        SipAccountInfo loginParam = new SipAccountInfo();
        loginParam.setLocalIpAddress(LoginCenter.getInstance().getLocalIPAddress());

        loginParam.setProxyServerAddr(regServerAddr);
        loginParam.setProxyServerPort(Integer.valueOf(regServerPort).intValue());
        loginParam.setRegisterServerAddr(regServerAddr);
        loginParam.setRegisterServerPort(Integer.valueOf(regServerPort).intValue());

        loginParam.setSipName(loginUportalAuthorizeResult.getSipAccount());
        loginParam.setSipImpi(loginUportalAuthorizeResult.getSipImpi());
        loginParam.setSipPassword(loginUportalAuthorizeResult.getSipPassword());
        loginParam.setDomain(loginUportalAuthorizeResult.getSipDomain());
        loginParam.setSipAuthPasswordType(loginUportalAuthorizeResult.getPasswordType());

        String[] sipAccount = loginUportalAuthorizeResult.getSipImpi().split("@");
        String sipNumber = sipAccount[0];
        loginParam.setSipNumber(sipNumber);

        return loginParam;
    }

    /**
     * This method is used to get firewall detect server information.
     * 获取防火墙探测服务器信息
     * @param loginUportalAuthorizeResult Indicates login authorize result
     *                                    鉴权登陆结果信息
     * @return LoginDetectServer Return the firewall detect server information
     *                           返回防火墙探测服务器信息
     */
    public LoginDetectServer getDetectServerFromAuthResult(LoginUportalAuthorizeResult loginUportalAuthorizeResult) {
        List<String> svnList = new ArrayList<>();

        List<LoginAuthorizeSiteInfo> siteInfo = loginUportalAuthorizeResult.getSiteInfo();
        Iterator siteInfoIterator = siteInfo.iterator();
        while (siteInfoIterator.hasNext()) {
            LoginAuthorizeSiteInfo loginAuthorizeSiteInfo = (LoginAuthorizeSiteInfo) siteInfoIterator.next();
            List<LoginAccessServer> accessServer = loginAuthorizeSiteInfo.getAccessServer();

            Iterator accessServerIterator = accessServer.iterator();
            while (accessServerIterator.hasNext()) {
                LoginAccessServer loginAccessServer = (LoginAccessServer) accessServerIterator.next();

                String svn = loginAccessServer.getSvnUri();
                svnList.add(svn);
            }
        }

        if (svnList.size() > 0) {
            return getFireDetectServer(svnList);
        }

        return null;
    }

    /**
     * This method is used to get stg information.
     * 获取STG信息
     * @param loginUportalAuthorizeResult
     * @return
     */
    public LoginStgParam getLoginStgParamFromAuthResult(LoginUportalAuthorizeResult loginUportalAuthorizeResult) {
        //待实现
        return null;
    }

    /**
     * This method is used to build stg tunnel.
     * [cn]创建stg通道
     * @param stgParam Indicates stg server
     *                 stg服务器信息
     * @return
     */
    public int buildStgTunnel(LoginStgParam stgParam) {
        //待实现
        return 0;
    }

    /**
     * This method is used to refresh token.
     * 更新token凭证
     * @param token Indicates token
     *              token值
     */
    public void updateToken(String token) {

        if (TupMgr.getInstance().getFeatureMgr().isSupportAudioAndVideoConf()) {
            //do nothing
        }

        if (TupMgr.getInstance().getFeatureMgr().isSupportAudioAndVideoConf()) {
            TupMgr.getInstance().getConfManagerIns().setAuthToken(token);
        }

        if (TupMgr.getInstance().getFeatureMgr().isSupportAddressbook()) {
            this.entAddressBookConfigInfo.setToken(token);
        }

        if (TupMgr.getInstance().getFeatureMgr().isSupportCTD()) {
            this.ctdCfgInfo.setToken(token);
        }
    }

    /**
     * This method is used to get firewall detect server.
     * 获取防火墙探测服务器
     * @param svnUriList Indicates the list of svn service
     *                   svn服务器地址列表
     * @return LoginDetectServer Return the firewall detect server information
     *                           返回防火墙探测服务器信息
     */
    private LoginDetectServer getFireDetectServer(List<String> svnUriList) {
        List<LoginSingleServerInfo> serverList = new ArrayList<>();

        if (svnUriList != null && !svnUriList.isEmpty()) {
            for (String svn : svnUriList) {
                if (TextUtils.isEmpty(svn)) {
                    continue;
                }

                if (svn.contains(":")) {
                    String[] svnAddress = svn.split(":");

                    if (svnAddress.length > 1) {
                        svn = svnAddress[0];
                    }
                }

                serverList.add(new LoginSingleServerInfo(svn, 0));
            }
        }

        if (serverList.size() > 0) {
            LoginDetectServer detectServer = new LoginDetectServer(serverList.size(), serverList);
            return detectServer;
        }

        return null;
    }

    public String getLocalIPAddress() {
        return localIPAddress;
    }

    public void setLocalIPAddress(String localIPAddress) {
        this.localIPAddress = localIPAddress;
    }

    public SipAccountInfo getSipAccountInfo() {
        return sipAccountInfo;
    }

    public void setSipAccountInfo(SipAccountInfo sipAccountInfo) {
        this.sipAccountInfo = sipAccountInfo;
    }

    public ImAccountInfo getImAccountInfo() {
        return imAccountInfo;
    }

    public void setImAccountInfo(ImAccountInfo imAccountInfo) {
        this.imAccountInfo = imAccountInfo;
    }

    public LoginStatus getLoginStatus() {
        return loginStatus;
    }

    public String getAccount() {
        return account;
    }

    public String getLoginServerAddress() {
        return loginServerAddress;
    }

    public int getLoginServerPort() {
        return loginServerPort;
    }

    public int getDeployMode() {
        return deployMode;
    }

    public void setDeployMode(int deployMode) {
        this.deployMode = deployMode;
    }

    public int getSolution() {
        return solution;
    }

    public void setSolution(int solution) {
        this.solution = solution;
    }


    public ConfConfigInfo getConfConfigInfo() {
        return confConfigInfo;
    }

    public void setConfConfigInfo(ConfConfigInfo confConfigInfo) {
        this.confConfigInfo = confConfigInfo;
    }

    public CtdConfigInfo getCtdCfgInfo() {
        return ctdCfgInfo;
    }

    public void setCtdCfgInfo(CtdConfigInfo ctdCfgInfo) {
        this.ctdCfgInfo = ctdCfgInfo;
    }

    public EntAddressBookConfigInfo getEntAddressBookConfigInfo() {
        return entAddressBookConfigInfo;
    }

    public void setEntAddressBookConfigInfo(EntAddressBookConfigInfo entAddressBookConfigInfo) {
        this.entAddressBookConfigInfo = entAddressBookConfigInfo;
    }

    public LoginFirewallMode getFirewallMode() {
        return firewallMode;
    }

    public void setFirewallMode(LoginFirewallMode firewallMode) {
        this.firewallMode = firewallMode;
    }


    public LoginStgParam getStgParam() {
        return stgParam;
    }

    public void setStgParam(LoginStgParam stgParam) {
        this.stgParam = stgParam;
    }


    public TupEaddrContactorInfo getSelfInfo() {
        return selfInfo;
    }

    public void setSelfInfo(TupEaddrContactorInfo selfInfo) {
        this.selfInfo = selfInfo;
    }


}
