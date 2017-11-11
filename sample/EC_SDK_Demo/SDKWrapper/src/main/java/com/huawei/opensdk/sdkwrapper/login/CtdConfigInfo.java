package com.huawei.opensdk.sdkwrapper.login;

/**
 * This class is about setting CTD server parameters.
 * CTD配置相关参数类
 */
public class CtdConfigInfo {

    /**
     * server address : Authentication Login Results obtained
     * 服务器地址，从鉴权登陆结果中获取
     */
    private String serverAddr;

    /**
     * server port : Authentication Login Results obtained
     * 服务器端口号，从鉴权登陆结果中获取
     */
    private int serverPort;

    /**
     * token: Authentication Login Results obtained
     * token值，从鉴权登陆结果中获取
     */
    private String token;

    /**
     * Caller number
     * 主叫号码
     */
    private String callerNumber;

    public String getServerAddr() {
        return serverAddr;
    }

    public void setServerAddr(String serverAddr) {
        this.serverAddr = serverAddr;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCallerNumber() {
        return callerNumber;
    }

    public void setCallerNumber(String callerNumber) {
        this.callerNumber = callerNumber;
    }

}
