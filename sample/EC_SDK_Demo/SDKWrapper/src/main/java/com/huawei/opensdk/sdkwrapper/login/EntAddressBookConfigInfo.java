package com.huawei.opensdk.sdkwrapper.login;

/**
 * This class is about setting enterprise address book server parameters.
 * 企业通讯录功能配置信息参数类(该信息都是从uPortal鉴权登陆回调消息中取到的)
 */
public class EntAddressBookConfigInfo {

    /**
     * Token
     * token值
     */
    private String token;

    /**
     * SIP IP Multimedia Private Identity
     * SIP IP多媒体私有标识
     */
    private String impi;

    /**
     * Register server address
     * 注册服务器地址
     */
    private String reg_server;

    /**
     * Register Server port number
     * 注册服务器端口号
     */
    private int reg_port;

    public int getReg_port() {
        return reg_port;
    }

    public void setReg_port(int reg_port) {
        this.reg_port = reg_port;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getImpi() {
        return impi;
    }

    public void setImpi(String impi) {
        this.impi = impi;
    }

    public String getReg_server() {
        return reg_server;
    }

    public void setReg_server(String reg_server) {
        this.reg_server = reg_server;
    }
}
