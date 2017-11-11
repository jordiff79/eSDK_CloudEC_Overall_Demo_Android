package com.huawei.opensdk.demoservice;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;

import com.huawei.opensdk.sdkwrapper.login.LoginCenter;
import com.huawei.opensdk.sdkwrapper.manager.TupMgr;
import com.huawei.tup.confctrl.ConfctrlAttendee;
import com.huawei.tup.confctrl.ConfctrlConfMode;
import com.huawei.tup.confctrl.ConfctrlConfRight;
import com.huawei.tup.confctrl.ConfctrlConfType;
import com.huawei.tup.confctrl.sdk.TupConfAttendeeOptResult;
import com.huawei.tup.confctrl.sdk.TupConfBaseAttendeeInfo;
import com.huawei.tup.confctrl.sdk.TupConfBookECConfInfo;
import com.huawei.tup.confctrl.sdk.TupConfECAttendeeInfo;
import com.huawei.tup.confctrl.sdk.TupConfGetConfInfo;
import com.huawei.tup.confctrl.sdk.TupConfGetConfList;
import com.huawei.tup.confctrl.sdk.TupConfInfo;
import com.huawei.tup.confctrl.sdk.TupConfNotifyBase;
import com.huawei.tup.confctrl.sdk.TupConfOptResult;
import com.huawei.tup.confctrl.sdk.TupConfParam;
import com.huawei.tup.confctrl.sdk.TupConfSpeakerInfo;
import com.huawei.tup.confctrl.sdk.TupConfctrlDataconfParams;
import com.huawei.tup.confctrl.sdk.TupConference;
import com.huawei.tup.eaddr.TupEaddrContactorInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is about meeting function management.
 * 会议服务管理类
 */
public class MeetingMgr extends TupConfNotifyBase implements IMeetingMgr{

    private static final String TAG = MeetingMgr.class.getSimpleName();

    private static MeetingMgr mInstance;

    /**
     * UI回调
     */
    private IConfNotification mConfNotification;

    /**
     * 预约会议状态
     */
    private ConfConstant.BookConfStatus currentBookConfStatus;

    /**
     * 当前正在召开的会议
     */
    private MeetingInstance currentConference;

    /**
     * 自己加入会议的号码
     */
    private String joinConfNumber;

    private MeetingMgr()
    {
    }

    public static MeetingMgr getInstance()
    {
        if (mInstance == null)
        {
            mInstance = new MeetingMgr();
        }
        return mInstance;
    }

    public void regConfServiceNotification(IConfNotification confNotification)
    {
        this.mConfNotification = confNotification;
    }


    public String getJoinConfNumber() {
        return joinConfNumber;
    }

    public void setJoinConfNumber(String joinConfNumber) {
        this.joinConfNumber = joinConfNumber;
    }


    public boolean isInConference() {
        if (null == currentConference)
        {
            return false;
        }
        return true;
    }

    public int getCurrentConferenceCallID() {
        if (null == currentConference)
        {
            return 0;
        }
        return currentConference.getCallID();
    }

    public void setCurrentConferenceCallID(int callID) {
        if (null == currentConference)
        {
            return;
        }
        currentConference.setCallID(callID);
    }

    public List<Member> getCurrentConferenceMemberList() {
        if (null == currentConference)
        {
            return null;
        }
        return currentConference.getMemberList();
    }

    public Member getCurrentConferenceSelf() {
        if (null == currentConference)
        {
            return null;
        }
        return currentConference.getSelf();
    }

    public ConfBaseInfo getCurrentConferenceBaseInfo() {
        if (null == currentConference)
        {
            return null;
        }
        return currentConference.getConfBaseInfo();
    }

    public void saveCurrentConferenceGetDataConfParamInfo(QueryJoinDataConfParamInfo info) {
        if (null == currentConference)
        {
            return;
        }

        if (getCurrentConferenceBaseInfo().getConfID().equals(info.getConfId()))
        {
            currentConference.setQueryJoinDataConfParamInfo(info);
        }
    }

    public void updateCurrentConferenceBaseInfo(ConfDetailInfo confDetailInfo) {
        if (null == currentConference)
        {
            return;
        }

        if (getCurrentConferenceBaseInfo().getConfID().equals(confDetailInfo.getConfID()))
        {
            currentConference.updateConfInfo(confDetailInfo);
        }
    }

    public boolean isInDataConf()
    {
        if (null == currentConference)
        {
            return false;
        }

        return true;
    }


    /**
     * This method is used to book instant conference or reserved conference
     * @param bookConferenceInfo 创会信息
     * @return
     */
    public int bookConference(BookConferenceInfo bookConferenceInfo)
    {
        Log.i(TAG, "bookConference.");

        if (bookConferenceInfo == null)
        {
            Log.e(TAG, "booKConferenceInfo obj is null");
            return -1;
        }

        boolean isInstantConference = bookConferenceInfo.isInstantConference();
        TupConfBookECConfInfo bookInfo = new TupConfBookECConfInfo();

        bookInfo.setSubject(bookConferenceInfo.getSubject());
        bookInfo.setConfType(ConfctrlConfType.CONFCTRL_E_CONF_TYPE_NORMAL);
        bookInfo.setMediaType(ConfConvertUtil.convertConfMediaType(bookConferenceInfo.getMediaType()));
        bookInfo.setStartTime(bookConferenceInfo.getStartTime());
        bookInfo.setConfLen(bookConferenceInfo.getDuration());
        bookInfo.setSize(bookConferenceInfo.getSize());

        List<ConfctrlAttendee> attendeeList = ConfConvertUtil.memberListToAttendeeList(bookConferenceInfo.getMemberList(), isInstantConference);
        bookInfo.setAttendee(attendeeList);
        bookInfo.setNumOfAttendee(attendeeList.size());

        //立即会议则设置自动延长
        if (bookConferenceInfo.isInstantConference())
        {
            bookInfo.setAutoProlong(1);
        }

        //其他参数可选，使用默认值即可

        int result = TupMgr.getInstance().getConfManagerIns().bookReservedConf(bookInfo);
        if (result != 0)
        {
            Log.e(TAG, "bookReservedConf result ->" + result);
            return  result;
        }

        //记录当前预约会议状态
        if (bookConferenceInfo.isInstantConference())
        {
            this.currentBookConfStatus = ConfConstant.BookConfStatus.INSTANT_BOOKING;
            setJoinConfNumber(bookConferenceInfo.getMemberList().get(0).getNumber());
        }
        else
        {
            this.currentBookConfStatus = ConfConstant.BookConfStatus.RESERVED_BOOKING;
        }

        return 0;
    }

    /**
     * This method is used to query my conference list
     * 查询会议列表
     * @param myRight 会议类型
     * @return
     */
    public int queryMyConfList(ConfConstant.ConfRight myRight)
    {
        Log.i(TAG, "query my conf list.");

        ConfctrlConfRight tupConfRight;
        switch (myRight)
        {
            case MY_CREATE:
                tupConfRight = ConfctrlConfRight.CONFCTRL_E_CONFRIGHT_CREATE;
                break;

            case MY_JOIN:
                tupConfRight = ConfctrlConfRight.CONFCTRL_E_CONFRIGHT_JOIN;
                break;

            case MY_CREATE_AND_JOIN:
                tupConfRight = ConfctrlConfRight.CONFCTRL_E_CONFRIGHT_CREATE_JOIN;
                break;
            default:
                tupConfRight = ConfctrlConfRight.CONFCTRL_E_CONFRIGHT_CREATE_JOIN;
                break;
        }

        TupConfGetConfList tupConfGetConfList = new TupConfGetConfList();

        tupConfGetConfList.setAccountId(""); //客户端仅查询自己，无需填写账号
        tupConfGetConfList.setConfId("");
        tupConfGetConfList.setSubject("");
        tupConfGetConfList.setPageSize(ConfConstant.PAGE_SIZE);
        tupConfGetConfList.setPageIndex(1); //当前Demo只查询一页，实际可根据需要分多页查询
        tupConfGetConfList.setIncludeEnd(0); //不包含已结束的会议
        tupConfGetConfList.setConfRight(tupConfRight);

        int result = TupMgr.getInstance().getConfManagerIns().getConfList(tupConfGetConfList);
        if (result != 0)
        {
            Log.e(TAG, "getConfList result ->" + result);
            return  result;
        }
        return 0;
    }

    /**
     * This method is used to query the conference detail
     * 查询会议详情
     * @param confID
     * @return
     */
    public int queryConfDetail(String confID)
    {
        Log.i(TAG,  "query conf detail");

        TupConfGetConfInfo tupConfGetConfInfo = new TupConfGetConfInfo();
        tupConfGetConfInfo.setConfId(confID);

        int result = TupMgr.getInstance().getConfManagerIns().getConfInfo(tupConfGetConfInfo);
        if (result != 0)
        {
            Log.e(TAG, "getConfInfo result ->" + result);
            return  result;
        }
        return result;
    }


    /**
     * This method is used to join conference
     * 用于通过会议列表等方式主动入会议，入会后需要邀请自己的号码
     * @param confId              会议ID
     * @param password            会议接入密码
     * @return
     */
    public int joinConf(String confId, String password, ConfConstant.ConfRole role)
    {
        Log.i(TAG,  "join conf.");

        currentConference = new MeetingInstance();
        int result = currentConference.joinConf(confId, password, "", true);
        if (result != 0)
        {
            Log.e(TAG, "joinConf result ->" + result);
            currentConference = null;
            return result;
        }

        // 记录当前会议中自己的角色
        currentConference.setSelfRole(role);
        return 0;
    }

    /**
     * This method is used to join conference
     * 用于被邀请或IVR主动入会等方式加入时，入会后不需要邀请自己的号码
     * @param confId              会议ID
     * @param token               会议接入TOKEN
     * @return
     */
    public int joinConfByToken(String confId, String token, ConfConstant.ConfRole role)
    {
        Log.i(TAG,  "join conf.");

        currentConference = new MeetingInstance();
        int result = currentConference.joinConf(confId, "", token, false);
        if (result != 0)
        {
            Log.e(TAG, "joinConf result ->" + result);
            currentConference = null;
            return result;
        }

        // 记录当前会议中自己的角色
        currentConference.setSelfRole(role);
        return 0;
    }

    /**
     * This method is used to auto join conf
     *  自动加入会议
     *  创建立即会议成功后调用
     * @param confInfo
     * @return
     */
    private int autoJoinConf(TupConfInfo confInfo)
    {
        Log.i(TAG,  "auto join conf.");

        String confId = confInfo.getConfID();

        ConfConstant.ConfRole role = ConfConstant.ConfRole.CHAIRMAN;
        //自动入会时，有主席密码则用主席密码入会，无主席则用普通与会密码入会
        String password = confInfo.getChairmanPwd();
        if ((password == null) || (password.equals("")))
        {
            role = ConfConstant.ConfRole.ATTENDEE;
            password = confInfo.getGuestPwd();
        }

        //自动加入会议时，需要自动邀请自己
        return joinConf(confId, password, role);
    }

    /**
     * This method is used to add yourself
     * 添加自己
     * @return
     */
    private int addYourself()
    {
        Member attendeeInfo = new Member();

        TupEaddrContactorInfo self = LoginCenter.getInstance().getSelfInfo();
        if (self != null)
        {
            attendeeInfo.setDisplayName(self.getPersonName());
        }

        attendeeInfo.setNumber(getJoinConfNumber());
        ConfConstant.ConfRole role = currentConference.getSelfRole();
        attendeeInfo.setRole(role);

        return currentConference.addAttendee(attendeeInfo);
    }

    /**
     * This method is used to leave conf
     * 离会
     * @return
     */
    public int leaveConf()
    {
        if (null == currentConference)
        {
            Log.i(TAG,  "leave conf, currentConference is null ");
            return 0;
        }

        currentConference.removeAttendee(getCurrentConferenceSelf());

        int result = currentConference.leaveConf();
        if (result == 0) {
            currentConference = null;
        }

        return result;
    }

    /**
     * This method is used to end conf
     * 结束会议
     * @return
     */
    public int endConf()
    {
        if (null == currentConference)
        {
            Log.i(TAG,  "end conf, currentConference is null ");
            return 0;
        }

        int result =  currentConference.endConf();
        if (result == 0) {
            currentConference = null;
        }

        return result;
    }

    /**
     * This method is used to add attendee
     * 添加与会者
     * @param attendee 与会者信息
     * @return
     */
    public int addAttendee(Member attendee)
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "add attendee failed, currentConference is null ");
            return -1;
        }

        int result =  currentConference.addAttendee(attendee);

        //TODO
        return result;
    }

    public int addAttendee(String attendeeNumber)
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "add attendee failed, currentConference is null ");
            return -1;
        }


        int result =  currentConference.addAttendee(attendeeNumber);

        //TODO
        return result;
    }

    /**
     * This method is used to remove attendee
     * 移除与会者
     * @param attendee 与会者信息
     * @return
     */
    public int removeAttendee(Member attendee)
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "remove attendee failed, currentConference is null ");
            return -1;
        }

        int result =  currentConference.removeAttendee(attendee);

        //TODO
        return result;
    }

    /**
     * This method is used to hang up attendee
     * 挂断与会者
     * @param attendee 与会者信息
     * @return
     */
    public int hangupAttendee(Member attendee)
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "hangup attendee failed, currentConference is null ");
            return -1;
        }

        int result =  currentConference.hangupAttendee(attendee);

        //TODO
        return result;
    }

    /**
     * This method is used to lock conf
     * 锁定会议
     * @param isLock 是否锁定
     * @return
     */
    public int lockConf(boolean isLock)
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "mute conf failed, currentConference is null ");
            return -1;
        }

        int result =  currentConference.lockConf(isLock);

        //TODO
        return result;
    }

    /**
     * This method is used to mute attendee
     * 静音与会者
     * @param attendee 与会者信息
     * @param isMute 是否静音
     * @return
     */
    public int muteAttendee(Member attendee, boolean isMute)
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "mute attendee failed, currentConference is null ");
            return -1;
        }

        int result =  currentConference.muteAttendee(attendee, isMute);

        //TODO
        return result;
    }

    /**
     * This method is used to mute conf
     * 静音会议
     * @param isMute 是否静音
     * @return
     */
    public int muteConf(boolean isMute)
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "mute conf failed, currentConference is null ");
            return -1;
        }

        int result =  currentConference.muteConf(isMute);

        //TODO
        return result;
    }

    /**
     * This method is used to release chairman
     * 是否主席
     * @return
     */
    public int releaseChairman()
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "release chairman failed, currentConference is null ");
            return -1;
        }

        int result =  currentConference.releaseChairman();

        //TODO
        return result;
    }

    /**
     * This method is used to request chairman
     * 请求主席
     * @param chairmanPassword 主席密码
     * @return
     */
    public int requestChairman(String chairmanPassword)
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "request chairman failed, currentConference is null ");
            return -1;
        }

        int result =  currentConference.requestChairman(chairmanPassword);

        //TODO
        return result;
    }

    public int setPresenter(Member attendee)
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "set presenter failed, currentConference is null ");
            return -1;
        }

        int result =  currentConference.setPresenter(attendee);

        //TODO
        return result;
    }

    /**
     * This method is used to set host
     * 设置主席
     * @param attendee 与会者信息
     * @return
     */
    public int setHost(Member attendee)
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "set presenter failed, currentConference is null ");
            return -1;
        }

        int result =  currentConference.setHost(attendee);

        //TODO
        return result;
    }

    /**
     * This method is used to hand up
     * 设置举手
     * @param handUp 是否举手
     * @param attendee 与会者信息
     * @return
     */
    public int handup(boolean handUp, Member attendee)
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "request chairman failed, currentConference is null ");
            return -1;
        }

        int result =  currentConference.handup(handUp, attendee);

        //TODO
        return result;
    }

    /**
     * This method is used to postpone conf
     * 延长会议
     * @param time 延长时长
     * @return
     */
    public int postpone(int time)
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "postpone conf failed, currentConference is null ");
            return -1;
        }

        int result =  currentConference.postpone(time);

        //TODO
        return result;
    }

    /**
     * This method is used to set conf mode
     * 设置会议类型
     * @param confctrlConfMode 会议类型
     * @return
     */
    public int setConfMode(ConfctrlConfMode confctrlConfMode)
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "set conf mode failed, currentConference is null ");
            return -1;
        }

        int result =  currentConference.setConfMode(confctrlConfMode);

        //TODO
        return result;
    }

    /**
     * This method is used to broadcast attendee
     * 广播与会者
     * @param attendee 与会者信息
     * @param isBroadcast 是否广播
     * @return
     */
    public int broadcastAttendee(Member attendee, boolean isBroadcast)
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "broadcast attendee failed, currentConference is null ");
            return -1;
        }

        int result =  currentConference.broadcastAttendee(attendee, isBroadcast);

        //TODO
        return result;
    }

    /**
     * This method is used to watch attendee
     * 观看与会者
     * @param attendee 与会者信息
     * @return
     */
    public int watchAttendee(Member attendee)
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "broadcast attendee failed, currentConference is null ");
            return -1;
        }

        int result =  currentConference.watchAttendee(attendee);

        //TODO
        return result;
    }

    /**
     * This method is used to upgrade conf
     * 会议升级
     * @return
     */
    public int upgradeConf()
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "upgrade conf failed, currentConference is null ");
            return -1;
        }

        int result =  currentConference.upgradeConf();

        //TODO
        return result;
    }

    /**
     * This method is used to join data conf
     * 加入数据会议
     * @return
     */
    public int joinDataConf()
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "join data conf failed, currentConference is null ");
            return -1;
        }

        int result =  currentConference.joinDataConf(mConfNotification);

        //TODO
        return result;
    }

    /**
     * 升级为数据会议前检查
     */
    public void checkUpgradeDataConf()
    {
        if (null == currentConference)
        {
            return;
        }

        currentConference.checkUpgradeDataConf();
    }

    public boolean judgeInviteFormMySelf(String confID)
    {
        if ((confID == null) || (confID.equals("")))
        {
            return false;
        }

        //TODO
        if (currentConference != null && getCurrentConferenceBaseInfo().getConfID().equals(confID))
        {
            return true;
        }

        return false;
    }

    private Member getMemberByNumber(String number)
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "upgrade conf failed, currentConference is null ");
            return null;
        }
        return currentConference.getMemberByNumber(number);
    }

    public void attachSurfaceView(ViewGroup container, Context context)
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "attach surface view failed, currentConference is null ");
            return;
        }
        currentConference.attachSurfaceView(container, context);
    }

    public boolean switchCamera()
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "switch camera failed, currentConference is null ");
            return false;
        }
        return currentConference.switchCamera();
    }

    public boolean openLocalVideo()
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "open local video failed, currentConference is null ");
            return false;
        }
        return currentConference.openLocalVideo();
    }

    public boolean closeLocalVideo()
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "close local video failed, currentConference is null ");
            return false;
        }
        return currentConference.closeLocalVideo();
    }

    public void setVideoContainer(Context context, ViewGroup localView, ViewGroup remoteView)
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "set video container failed, currentConference is null ");
            return;
        }
        currentConference.setVideoContainer(context, localView, remoteView);
    }

    public boolean attachRemoteVideo(long userID, long deviceID)
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "attach remote video failed, currentConference is null ");
            return false;
        }
        return currentConference.attachRemoteVideo(userID, deviceID);
    }

    public boolean detachRemoteVideo(long userID, long deviceID)
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "detach remote video failed, currentConference is null ");
            return false;
        }
        return currentConference.detachRemoteVideo(userID, deviceID);
    }

    public boolean videoOpen(long deviceID)
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "open video failed, currentConference is null ");
            return false;
        }
        return currentConference.videoOpen(deviceID);
    }

    public void leaveVideo()
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "leave video failed, currentConference is null ");
            return;
        }
        currentConference.leaveVideo();
    }

    public void changeLocalVideoVisible(boolean visible)
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "change local video visible failed, currentConference is null ");
            return;
        }
        currentConference.changeLocalVideoVisible(visible);
    }

    public boolean attachVideo(long userID, long deviceID)
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "attach video failed, currentConference is null ");
            return false;
        }
        return currentConference.attachVideo(userID, deviceID);
    }

    @Override
    public void onBookReservedConfResult(TupConfOptResult result, TupConfInfo confInfo)
    {
        Log.i(TAG, "onBookReservedConfResult");
        if ((result == null) || (confInfo == null))
        {
            Log.e(TAG, "book conference is failed, unknown error.");
            mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.BOOK_CONF_FAILED, -1);

            this.currentBookConfStatus = ConfConstant.BookConfStatus.IDLE;
            return;
        }

        if (result.getOptResult() != TupConfParam.CONF_RESULT.TUP_SUCCESS)
        {
            Log.e(TAG, "book conference is failed, return ->" + result.getOptResult());
            mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.BOOK_CONF_FAILED, result.getOptResult());
        }
        else
        {
            Log.i(TAG, "book conference is success.");
            mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.BOOK_CONF_SUCCESS, result.getOptResult());

            //若创建的是即时会议，则自动加入会议
            if (this.currentBookConfStatus == ConfConstant.BookConfStatus.INSTANT_BOOKING)
            {
                int ret = autoJoinConf(confInfo);
                if (ret != 0)
                {
                    mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.JOIN_CONF_FAILED, ret);
                }
            }
        }

        //无论成功失败，均清除状态
        this.currentBookConfStatus = ConfConstant.BookConfStatus.IDLE;
    }

    @Override
    public void onGetConfListResult(List<TupConfInfo> list, TupConfOptResult tupConfOptResult)
    {
        Log.i(TAG, "onGetConfListResult");
        if (tupConfOptResult == null)
        {
            Log.e(TAG, "get conference list is failed, unknown error.");
            mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.QUERY_CONF_LIST_FAILED, -1);
            return;
        }
        else if (tupConfOptResult.getOptResult() != 0)
        {
            Log.e(TAG, "get conference list is failed, return ->" + tupConfOptResult.getOptResult());
            mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.QUERY_CONF_LIST_FAILED, tupConfOptResult.getOptResult());
            return;
        }

        List<ConfBaseInfo> confBaseInfoList = new ArrayList<>();
        for (TupConfInfo confInfo: list)
        {
            ConfBaseInfo confBaseInfo = new ConfBaseInfo();
            confBaseInfo.setSize(confInfo.getSize());

            confBaseInfo.setConfID(confInfo.getConfID());
            confBaseInfo.setSubject(confInfo.getSubject());
            confBaseInfo.setAccessNumber(confInfo.getAccessNumber());
            confBaseInfo.setChairmanPwd(confInfo.getChairmanPwd());
            confBaseInfo.setGuestPwd(confInfo.getGuestPwd());
            confBaseInfo.setSchedulerNumber(confInfo.getScheduserNumber());
            confBaseInfo.setSchedulerName(confInfo.getScheduserName());
            confBaseInfo.setStartTime(confInfo.getStartTime());
            confBaseInfo.setEndTime(confInfo.getEndTime());

            confBaseInfo.setMediaType(ConfConvertUtil.convertConfMediaType(confInfo.getMediaType()));
            confBaseInfo.setConfState(ConfConvertUtil.convertConfctrlConfState(confInfo.getConfState()));

            confBaseInfoList.add(confBaseInfo);
        }

        mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.QUERY_CONF_LIST_SUCCESS, confBaseInfoList);
    }

    @Override
    public void onGetConfInfoResult(TupConfInfo confInfo, List<TupConfECAttendeeInfo> list,
                                    TupConfOptResult tupConfOptResult)
    {
        Log.i(TAG, "onGetConfInfoResult");
        if (tupConfOptResult == null)
        {
            Log.e(TAG, "get conference detail is failed, unknown error.");
            mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.QUERY_CONF_DETAIL_FAILED, -1);
            return;
        }
        else if (tupConfOptResult.getOptResult() != 0)
        {
            Log.e(TAG, "get conference detail is failed, return ->" + tupConfOptResult.getOptResult());
            mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.QUERY_CONF_DETAIL_FAILED, tupConfOptResult.getOptResult());
            return;
        }

        ConfDetailInfo confDetailInfo = new ConfDetailInfo();
        confDetailInfo.setSize(confInfo.getSize());

        confDetailInfo.setConfID(confInfo.getConfID());
        confDetailInfo.setSubject(confInfo.getSubject());
        confDetailInfo.setAccessNumber(confInfo.getAccessNumber());
        confDetailInfo.setChairmanPwd(confInfo.getChairmanPwd());
        confDetailInfo.setGuestPwd(confInfo.getGuestPwd());
        confDetailInfo.setSchedulerNumber(confInfo.getScheduserNumber());
        confDetailInfo.setSchedulerName(confInfo.getScheduserName());
        confDetailInfo.setStartTime(confInfo.getStartTime());
        confDetailInfo.setEndTime(confInfo.getEndTime());

        confDetailInfo.setMediaType(ConfConvertUtil.convertConfMediaType(confInfo.getMediaType()));
        confDetailInfo.setConfState(ConfConvertUtil.convertConfctrlConfState(confInfo.getConfState()));

        List<Member> memberList = ConfConvertUtil.convertAttendeeInfoList(list);
        confDetailInfo.setMemberList(memberList);

        mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.QUERY_CONF_DETAIL_SUCCESS, confDetailInfo);
    }

    @Override
    public void onSubscribeConfResult(TupConfOptResult tupConfOptResult)
    {
        Log.i(TAG, "onSubscribeConfResult");
        if ((currentConference == null) || (tupConfOptResult == null)) {
            return;
        }

        int result = tupConfOptResult.getOptResult();
        if (result == 0)
        {
            //TupConference tupConference = TupMgr.getInstance().getConfManagerIns().getConfByConfHandle(tupConfOptResult.getConfHandle());
            String confID = currentConference.getConfBaseInfo().getConfID();
            mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.JOIN_CONF_SUCCESS, confID);

            //暂不判断环境类型，无论CloudPBX还是CloudEC，均申请会控权限
            //ConfctrlConfEnvType confEnvType = LoginCenter.getInstance().getConfConfigInfo().getConfEnvType();
            String number = LoginCenter.getInstance().getSipAccountInfo().getTerminal();
            result = currentConference.requestConfRight(number);
            if (result != 0)
            {
                Log.e(TAG, "requestConfRight ->" + result);
                mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.REQUEST_RIGHT_FAILED, result);
            }
        }
        else
        {
            mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.JOIN_CONF_FAILED, result);
        }
    }


    @Override
    public void onRequestConfRightResult(TupConfOptResult tupConfOptResult, TupConference tupConference) {
        Log.i(TAG, "onRequestConfRightResult");
        if (currentConference == null)
        {
            return;
        }

        if ((tupConfOptResult == null) || (tupConference == null))
        {
            Log.e(TAG, "request conf right is failed, unknown error.");
            mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.REQUEST_RIGHT_FAILED, -1);
            return;
        }
        else if (tupConfOptResult.getOptResult() != 0)
        {
            Log.e(TAG, "request conf right is failed, return ->" + tupConfOptResult.getOptResult());
            mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.REQUEST_RIGHT_FAILED, tupConfOptResult.getOptResult());
            return;
        }

        //如果需要邀请自己，则邀请自己
        if (currentConference.isNeedInviteYourself() == true)
        {
            int result = addYourself();
            if (result != 0)
            {
                Log.e(TAG, "addYourself: addECAttendee result->" + result);
                mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.ADD_YOURSELF_FAILED, result);
            }
        }
    }


    @Override
    public void onConfStatusUpdateInd(TupConfInfo tupConfInfo, List<TupConfECAttendeeInfo> list, int participantUpdateType) {
        Log.i(TAG, "onConfStatusUpdateInd");
        if ((currentConference == null) || (tupConfInfo == null))
        {
            return;
        }

        String confID = tupConfInfo.getConfStatusInfo().getConfId();
        if (confID == null)
        {
            return;
        }

        if (!confID.equals(currentConference.getConfBaseInfo().getConfID()))
        {
            return;
        }

        //更新保存会议状态信息
        currentConference.updateConfInfo(tupConfInfo, list, participantUpdateType);
        currentConference.checkUpgradeDataConf();

        mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.STATE_UPDATE, confID);

    }

    @Override
    public void onAddAttendeeResult(TupConfAttendeeOptResult tupConfAttendeeOptResult) {
        Log.i(TAG, "onAddAttendeeResult");
        if ((currentConference == null) || (tupConfAttendeeOptResult == null)) {
            return;
        }

        mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.ADD_ATTENDEE_RESULT, tupConfAttendeeOptResult.getOptResult());
    }

    @Override
    public void onDelAttendeeResult(TupConfAttendeeOptResult tupConfAttendeeOptResult) {
        Log.i(TAG, "onDelAttendeeResult");
        if ((currentConference == null) || (tupConfAttendeeOptResult == null)) {
            return;
        }

        mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.DEL_ATTENDEE_RESULT, tupConfAttendeeOptResult.getOptResult());
    }

    @Override
    public void onMuteAttendeeResult(TupConfAttendeeOptResult tupConfAttendeeOptResult, boolean isMute)
    {
        Log.i(TAG, "onMuteAttendeeResult");
        if ((currentConference == null) || (tupConfAttendeeOptResult == null)) {
            return;
        }

        if (isMute)
        {
            mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.MUTE_ATTENDEE_RESULT, tupConfAttendeeOptResult.getOptResult());
        }
        else
        {
            mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.UN_MUTE_ATTENDEE_RESULT, tupConfAttendeeOptResult.getOptResult());
        }
    }

    @Override
    public void onMuteConfResult(TupConfOptResult tupConfOptResult, boolean isMute) {
        Log.i(TAG, "onMuteConfResult");
        if ((currentConference == null) || (tupConfOptResult == null)) {
            return;
        }

        if (isMute)
        {
            mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.MUTE_CONF_RESULT, tupConfOptResult.getOptResult());
        }
        else
        {
            mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.UN_MUTE_CONF_RESULT, tupConfOptResult.getOptResult());
        }
    }

    @Override
    public void onLockConfResult(TupConfOptResult tupConfOptResult, boolean isLock) {
        Log.i(TAG, "onLockConfResult");
        if ((currentConference == null) || (tupConfOptResult == null)) {
            return;
        }

        if (isLock)
        {
            mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.LOCK_CONF_RESULT, tupConfOptResult.getOptResult());
        }
        else
        {
            mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.UN_LOCK_CONF_RESULT, tupConfOptResult.getOptResult());
        }
    }

    @Override
    public void onHandupResult(TupConfOptResult tupConfOptResult, boolean isHandup) {
        Log.i(TAG, "onHandupResult");
        if ((currentConference == null) || (tupConfOptResult == null)) {
            return;
        }

        if (isHandup)
        {
            mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.HAND_UP_RESULT, tupConfOptResult.getOptResult());
        }
        else
        {
            mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.CANCEL_HAND_UP_RESULT, tupConfOptResult.getOptResult());
        }
    }


    @Override
    public void onHanddownAttendeeResult(TupConfAttendeeOptResult tupConfAttendeeOptResult) {
        Log.i(TAG, "onHanddownAttendeeResult");
        if ((currentConference == null) || (tupConfAttendeeOptResult == null)) {
            return;
        }

        mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.CANCEL_HAND_UP_RESULT, tupConfAttendeeOptResult.getOptResult());
    }


    @Override
    public void onConfWillTimeOutInd(TupConference conf) {
        Log.i(TAG, "onConfWillTimeOutInd");
        if ((currentConference == null) || (conf == null)) {
            return;
        }

        int time = conf.getRemainingTime();
        mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.WILL_TIMEOUT, time);
    }

    @Override
    public void onConfPostponeResult(TupConfOptResult tupConfOptResult) {
        Log.i(TAG, "onConfPostponeResult");
        if ((currentConference == null) || (tupConfOptResult == null)) {
            return;
        }

        mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.POSTPONE_CONF_RESULT, tupConfOptResult.getOptResult());
    }

    @Override
    public void onSpeakerListInd(TupConference tupConference, TupConfSpeakerInfo tupConfSpeakerInfo) {
        Log.i(TAG, "onSpeakerListInd");
        if ((currentConference == null) || (tupConference == null) || (tupConfSpeakerInfo == null)) {
            return;
        }

        List<Member> speakers = new ArrayList<>();
        List<TupConfBaseAttendeeInfo> speakerList = tupConfSpeakerInfo.getSpeakerList();
        for (TupConfBaseAttendeeInfo speaker : speakerList)
        {
            Member member = new Member();
            member.setNumber(speaker.getNumber());

            //根据号码在与会者列表中找到与会者，获取此与会者的名字
            Member temp = getMemberByNumber(speaker.getNumber());
            if (temp == null) {
                continue;
            }

            member.setDisplayName(temp.getDisplayName());
        }

        if (speakers.size() > 0)
        {
            mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.SPEAKER_LIST_IND, speakers);
        }
    }

    @Override
    public void onReqChairmanResult(TupConfOptResult tupConfOptResult) {
        Log.i(TAG, "onReqChairmanResult");
        if ((currentConference == null) || (tupConfOptResult == null)) {
            return;
        }

        mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.REQUEST_CHAIRMAN_RESULT, tupConfOptResult.getOptResult());
    }

    @Override
    public void onRealseChairmanResult(TupConfOptResult tupConfOptResult) {
        Log.i(TAG, "onRealseChairmanResult");
        if ((currentConference == null) || (tupConfOptResult == null)) {
            return;
        }

        mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.RELEASE_CHAIRMAN_RESULT, tupConfOptResult.getOptResult());
    }

    @Override
    public void onSetConfModeResult(TupConfOptResult tupConfOptResult) {
        Log.i(TAG, "onSetConfModeResult");
        if ((currentConference == null) || (tupConfOptResult == null)) {
            return;
        }

        mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.SET_CONF_MODE_RESULT, tupConfOptResult.getOptResult());
    }

    @Override
    public void onBroadcastAttendeeResult(TupConfOptResult tupConfOptResult) {
        Log.i(TAG, "onBroadcastAttendeeResult");
    }

    @Override
    public void onCancelBroadcastAttendeeResult(TupConfOptResult tupConfOptResult) {
        Log.i(TAG, "onCancelBroadcastAttendeeResult");
    }

    @Override
    public void onBroadcastAttendeeInd(TupConference tupConference, TupConfBaseAttendeeInfo tupConfBaseAttendeeInfo) {
        Log.i(TAG, "onBroadcastAttendeeInd");
    }

    @Override
    public void onCancelBroadcastAttendeeInd(TupConference tupConference, TupConfBaseAttendeeInfo tupConfBaseAttendeeInfo) {
        Log.i(TAG, "onCancelBroadcastAttendeeInd");
    }

    @Override
    public void onWatchAttendeeResult(TupConfOptResult tupConfOptResult) {
        Log.i(TAG, "onWatchAttendeeResult");
    }

    @Override
    public void onMultiPicResult(TupConfOptResult tupConfOptResult) {
        Log.i(TAG, "onMultiPicResult");
    }

    @Override
    public void onAttendeeBroadcastedInd(TupConference tupConference, TupConfBaseAttendeeInfo tupConfBaseAttendeeInfo) {
        Log.i(TAG, "onAttendeeBroadcastedInd");
    }

    @Override
    public void onLocalBroadcastStatusInd(TupConference tupConference, boolean b) {
        Log.i(TAG, "onLocalBroadcastStatusInd");
    }

    @Override
    public void onConfInfoInd(TupConference tupConference) {
        Log.i(TAG, "onConfInfoInd");
    }

    @Override
    public void onEndConfInd(TupConference tupConference) {
        Log.i(TAG, "onEndConfInd");
    }

    @Override
    public void onBeTransToConfInd(TupConference tupConference, int i) {
        Log.i(TAG, "onBeTransToConfInd");
    }

    @Override
    public void onEndConfResult(TupConfOptResult tupConfOptResult) {
        Log.i(TAG, "onEndConfResult");
    }

    @Override
    public void onCallAttendeeResult(TupConfAttendeeOptResult tupConfAttendeeOptResult) {
        Log.i(TAG, "onCallAttendeeResult");
    }

    @Override
    public void onHangupAttendeeResult(TupConfAttendeeOptResult tupConfAttendeeOptResult) {
        Log.i(TAG, "onHangupAttendeeResult");
    }

    @Override
    public void onTransToConfResult(TupConfOptResult tupConfOptResult, int i) {
        Log.i(TAG, "onTransToConfResult");
    }

    @Override
    public void onUpgradeConfResult(TupConfOptResult tupConfOptResult) {
        Log.i(TAG, "onUpgradeConfResult");

        if ((currentConference == null) || (tupConfOptResult == null)) {
            return;
        }

        // 升级会议成功，直接更新当前会议类型，防止服务器未及时推送会议状态更新消息
        if (tupConfOptResult.getOptResult() == 0) {
            ConfConstant.ConfMediaType confMediaType = currentConference.getConfBaseInfo().getMediaType();
            if (confMediaType == ConfConstant.ConfMediaType.VOICE_CONF) {
                currentConference.getConfBaseInfo().setMediaType(ConfConstant.ConfMediaType.VOICE_AND_DATA_CONF);
            } else if (confMediaType == ConfConstant.ConfMediaType.VIDEO_CONF){
                currentConference.getConfBaseInfo().setMediaType(ConfConstant.ConfMediaType.VIDEO_AND_DATA_CONF);
            }
        }

        mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.UPGRADE_CONF_RESULT, tupConfOptResult.getOptResult());
    }

    @Override
    public void onAuxtokenOwnerInd(TupConference tupConference, TupConfBaseAttendeeInfo tupConfBaseAttendeeInfo) {
        Log.i(TAG, "onAuxtokenOwnerInd");
    }

    @Override
    public void onAuxsendCmd(TupConference tupConference, boolean b) {
        Log.i(TAG, "onAuxsendCmd");
    }

    @Override
    public void onGetDataConfParamsResult(TupConfOptResult tupConfOptResult, TupConfctrlDataconfParams tupConfctrlDataconfParams) {
        Log.i(TAG, "onGetDataConfParamsResult");
        if ((currentConference == null) || (tupConfOptResult == null)) {
            return;
        }

        if (tupConfOptResult.getOptResult() == 0) {
            currentConference.setJoinDataConfParams(tupConfctrlDataconfParams);
        }

        mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.GET_DATA_CONF_PARAM_RESULT, tupConfOptResult.getOptResult());
    }

    @Override
    public void onAddDataConfInd(TupConfOptResult tupConfOptResult, TupConfInfo tupConfInfo) {
        Log.i(TAG, "onAddDataConfInd");
    }

}
