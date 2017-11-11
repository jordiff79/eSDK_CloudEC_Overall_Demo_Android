package com.huawei.opensdk.callmgr.iptService;

import com.huawei.opensdk.sdkwrapper.manager.TupMgr;

import java.util.List;

import common.TupCallParam;
import object.TupCallCfgSIP;
import object.TupServiceRightCfg;

/**
 * This class is about IPT manager
 * IPT管理类
 */
public class IptMgr {

    /**
     *  IPT Management object Instance
     * ipt管理类实例
     */
    private static IptMgr instance;

    /**
     * Ipt UI callback
     * ipt UI层回调
     */
    private IIptNotification iptNotification;

    /**
     * Free of disturbing
     * 免打扰
     */
    private IptRegisterInfo dndRegisterInfo;

    /**
     * Call wait
     * 呼叫等待
     */
    private IptRegisterInfo cwRegisterInfo;

    /**
     * Forward unconditionally
     * 无条件前转
     */
    private IptRegisterInfo cfuRegisterInfo;

    /**
     * Turn around before you are busy
     * 遇忙前转
     */
    private IptRegisterInfo cfbRegisterInfo;

    /**
     * Turn before answering
     * 无应答前转
     */
    private IptRegisterInfo cfnaRegisterInfo;

    /**
     * Before you go offline
     * 离线前转
     */
    private IptRegisterInfo cfnrRegisterInfo;


    public IptMgr() {
        dndRegisterInfo = new IptRegisterInfo();
        cwRegisterInfo = new IptRegisterInfo();
        cfuRegisterInfo = new IptRegisterInfo();
        cfbRegisterInfo = new IptRegisterInfo();
        cfnaRegisterInfo = new IptRegisterInfo();
        cfnrRegisterInfo = new IptRegisterInfo();
    }


    public static IptMgr getInstance() {
        if (null == instance) {
            instance = new IptMgr();
        }
        return instance;
    }


    /**
     * This method is used to get IPT Callback Object
     * 获取ipt回调对象
     * @return IIptNotification 返回回调对象
     */
    public IIptNotification getIptNotification() {
        return iptNotification;
    }

    /**
     * This method is used to registering callback Functions
     * 注册回调函数
     * @param iptNotification
     */
    public void regIptNotification(IIptNotification iptNotification)
    {
        this.iptNotification = iptNotification;
    }

    /**
     * This method is used to unregister callback function
     * 注销回调函数
     * @param iptNotification
     */
    public void unregIptNotification(IIptNotification iptNotification)
    {
        if (null != iptNotification)
        {
            iptNotification = null;
        }
    }

    /**
     * This method is used to init IPT config
     * 初始化ipt业务配置
     */
    public void configIPTServiceParam()
    {
        TupCallCfgSIP tupCallCfgSIP = TupMgr.getInstance().getTupCallCfgSIP();

        tupCallCfgSIP.setServrightDnd(TupCallParam.CALL_D_CFG_ID.CALL_D_CFG_ID_SERVRIGHT_DND, "*56*#", "#56#");
        tupCallCfgSIP.setServrightCfu(TupCallParam.CALL_D_CFG_ID.CALL_D_CFG_ID_SERVRIGHT_CFU, "**21*", "##21#");
        tupCallCfgSIP.setServrightCfb(TupCallParam.CALL_D_CFG_ID.CALL_D_CFG_ID_SERVRIGHT_CFB, "**67*", "##67#");
        tupCallCfgSIP.setServrightCfnr(TupCallParam.CALL_D_CFG_ID.CALL_D_CFG_ID_SERVRIGHT_CFNR, "**45*", "##45#");
        tupCallCfgSIP.setServrightCfna(TupCallParam.CALL_D_CFG_ID.CALL_D_CFG_ID_SERVRIGHT_CFNA, "**61*", "##61#");
        tupCallCfgSIP.setServrightCallwait(TupCallParam.CALL_D_CFG_ID.CALL_D_CFG_ID_SERVRIGHT_CALLWAIT, "*43#", "#43#");

        TupMgr.getInstance().getCallManagerIns().setCfgSIP(tupCallCfgSIP);
    }

    /**
     * This method is used to set up IPT Business
     * 设置ipt业务
     * @param callNumber    Forward number
     *                      前转号码
     * @param type        Business type, taking value reference Tupcallparam. call_d_cfg_id
     *                      业务类型，取值参考TupCallParam.CALL_D_CFG_ID
     */
    public void setIPTService(String callNumber, int type) {
        TupMgr.getInstance().getCallManagerIns().setCfgIPTService(type, callNumber);
    }

    /**
     * This method is used to get Functional permissions
     * 更新ipt业务状态
     * @param listRightCfg  Permissions List
     *                          权限列表
     */
    public void updateRegisterStatus(List<TupServiceRightCfg> listRightCfg)
    {
        dndRegisterInfo.setRegister(listRightCfg.get(TupCallParam.CALL_E_SERVICE_RIGHT_TYPE.CALL_E_SERVICE_RIGHT_TYPE_DONOTDISTURB).getRegister());
        dndRegisterInfo.setRight(listRightCfg.get(TupCallParam.CALL_E_SERVICE_RIGHT_TYPE.CALL_E_SERVICE_RIGHT_TYPE_DONOTDISTURB).getRight());
        dndRegisterInfo.setActiveAccessCode(listRightCfg.get(TupCallParam.CALL_E_SERVICE_RIGHT_TYPE.CALL_E_SERVICE_RIGHT_TYPE_DONOTDISTURB).getActiveAccessCode());
        dndRegisterInfo.setDeactiveAccessCode(listRightCfg.get(TupCallParam.CALL_E_SERVICE_RIGHT_TYPE.CALL_E_SERVICE_RIGHT_TYPE_DONOTDISTURB).getDeactiveAccessCode());

        cwRegisterInfo.setRegister(listRightCfg.get(TupCallParam.CALL_E_SERVICE_RIGHT_TYPE.CALL_E_SERVICE_RIGHT_TYPE_CALL_WAIT).getRegister());
        cwRegisterInfo.setRight(listRightCfg.get(TupCallParam.CALL_E_SERVICE_RIGHT_TYPE.CALL_E_SERVICE_RIGHT_TYPE_CALL_WAIT).getRight());
        cwRegisterInfo.setActiveAccessCode(listRightCfg.get(TupCallParam.CALL_E_SERVICE_RIGHT_TYPE.CALL_E_SERVICE_RIGHT_TYPE_CALL_WAIT).getActiveAccessCode());
        cwRegisterInfo.setDeactiveAccessCode(listRightCfg.get(TupCallParam.CALL_E_SERVICE_RIGHT_TYPE.CALL_E_SERVICE_RIGHT_TYPE_CALL_WAIT).getDeactiveAccessCode());

        cfuRegisterInfo.setRegister(listRightCfg.get(TupCallParam.CALL_E_SERVICE_RIGHT_TYPE.CALL_E_SERVICE_RIGHT_TYPE_CALLFORWARDING_UNCONDITIONAL).getRegister());
        cfuRegisterInfo.setRight(listRightCfg.get(TupCallParam.CALL_E_SERVICE_RIGHT_TYPE.CALL_E_SERVICE_RIGHT_TYPE_CALLFORWARDING_UNCONDITIONAL).getRight());
        cfuRegisterInfo.setActiveAccessCode(listRightCfg.get(TupCallParam.CALL_E_SERVICE_RIGHT_TYPE.CALL_E_SERVICE_RIGHT_TYPE_CALLFORWARDING_UNCONDITIONAL).getActiveAccessCode());
        cfuRegisterInfo.setDeactiveAccessCode(listRightCfg.get(TupCallParam.CALL_E_SERVICE_RIGHT_TYPE.CALL_E_SERVICE_RIGHT_TYPE_CALLFORWARDING_UNCONDITIONAL).getDeactiveAccessCode());

        cfbRegisterInfo.setRegister(listRightCfg.get(TupCallParam.CALL_E_SERVICE_RIGHT_TYPE.CALL_E_SERVICE_RIGHT_TYPE_CALLFORWARDING_ONBUSY).getRegister());
        cfbRegisterInfo.setRight(listRightCfg.get(TupCallParam.CALL_E_SERVICE_RIGHT_TYPE.CALL_E_SERVICE_RIGHT_TYPE_CALLFORWARDING_ONBUSY).getRight());
        cfbRegisterInfo.setActiveAccessCode(listRightCfg.get(TupCallParam.CALL_E_SERVICE_RIGHT_TYPE.CALL_E_SERVICE_RIGHT_TYPE_CALLFORWARDING_ONBUSY).getActiveAccessCode());
        cfbRegisterInfo.setDeactiveAccessCode(listRightCfg.get(TupCallParam.CALL_E_SERVICE_RIGHT_TYPE.CALL_E_SERVICE_RIGHT_TYPE_CALLFORWARDING_ONBUSY).getDeactiveAccessCode());

        cfnaRegisterInfo.setRegister(listRightCfg.get(TupCallParam.CALL_E_SERVICE_RIGHT_TYPE.CALL_E_SERVICE_RIGHT_TYPE_CALLFORWARDING_NOREPLY).getRegister());
        cfnaRegisterInfo.setRight(listRightCfg.get(TupCallParam.CALL_E_SERVICE_RIGHT_TYPE.CALL_E_SERVICE_RIGHT_TYPE_CALLFORWARDING_NOREPLY).getRight());
        cfnaRegisterInfo.setActiveAccessCode(listRightCfg.get(TupCallParam.CALL_E_SERVICE_RIGHT_TYPE.CALL_E_SERVICE_RIGHT_TYPE_CALLFORWARDING_NOREPLY).getActiveAccessCode());
        cfnaRegisterInfo.setDeactiveAccessCode(listRightCfg.get(TupCallParam.CALL_E_SERVICE_RIGHT_TYPE.CALL_E_SERVICE_RIGHT_TYPE_CALLFORWARDING_NOREPLY).getDeactiveAccessCode());

        cfnrRegisterInfo.setRegister(listRightCfg.get(TupCallParam.CALL_E_SERVICE_RIGHT_TYPE.CALL_E_SERVICE_RIGHT_TYPE_CALLFORWARDING_OFFLINE).getRegister());
        cfnrRegisterInfo.setRight(listRightCfg.get(TupCallParam.CALL_E_SERVICE_RIGHT_TYPE.CALL_E_SERVICE_RIGHT_TYPE_CALLFORWARDING_OFFLINE).getRight());
        cfnrRegisterInfo.setActiveAccessCode(listRightCfg.get(TupCallParam.CALL_E_SERVICE_RIGHT_TYPE.CALL_E_SERVICE_RIGHT_TYPE_CALLFORWARDING_OFFLINE).getActiveAccessCode());
        cfnrRegisterInfo.setDeactiveAccessCode(listRightCfg.get(TupCallParam.CALL_E_SERVICE_RIGHT_TYPE.CALL_E_SERVICE_RIGHT_TYPE_CALLFORWARDING_OFFLINE).getDeactiveAccessCode());
    }

    public IptRegisterInfo getDndRegisterInfo() {
        return dndRegisterInfo;
    }

    public IptRegisterInfo getCwRegisterInfo() {
        return cwRegisterInfo;
    }

    public IptRegisterInfo getCfuRegisterInfo() {
        return cfuRegisterInfo;
    }

    public IptRegisterInfo getCfbRegisterInfo() {
        return cfbRegisterInfo;
    }

    public IptRegisterInfo getCfnaRegisterInfo() {
        return cfnaRegisterInfo;
    }

    public IptRegisterInfo getCfnrRegisterInfo() {
        return cfnrRegisterInfo;
    }


}


