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
     * Querying a contact server address
     * 查询联系人服务器地址
     */
    private String contactServer;

    /**
     * Querying icon server address
     * 查询头像服务器地址
     */
    private String iconServer;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getContactServer() {
        return contactServer;
    }

    public void setContactServer(String contactServer) {
        this.contactServer = contactServer;
    }

    public String getIconServer() {
        return iconServer;
    }

    public void setIconServer(String iconServer) {
        this.iconServer = iconServer;
    }
}
