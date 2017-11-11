package com.huawei.opensdk.callmgr.iptService;

/**
 * This interface is about Ipt module and UI callback..
 * IPT模块UI回调
 */

public interface IIptNotification {

    void onSetIptServiceSuc(int serviceCallType);

    void onSetIptServiceFal(int serviceCallType);
}
