package com.huawei.opensdk.demoservice;


import com.huawei.tup.confctrl.ConfctrlAttendee;
import com.huawei.tup.confctrl.ConfctrlAttendeeType;
import com.huawei.tup.confctrl.ConfctrlConfMediatypeFlag;
import com.huawei.tup.confctrl.ConfctrlConfRole;
import com.huawei.tup.confctrl.ConfctrlConfState;
import com.huawei.tup.confctrl.ConfctrlParticipantStatus;
import com.huawei.tup.confctrl.sdk.TupConfECAttendeeInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is about conference Tools
 * 会议工具类
 */
public class ConfConvertUtil {

    /**
     * This method is used to convert conference media type.
     * 会议媒体类型转换
     * @param mediaType 媒体类型
     * @return
     */
    public static int convertConfMediaType(ConfConstant.ConfMediaType mediaType)
    {
        int confMediaType;
        switch (mediaType)
        {
            //音频
            case VOICE_CONF:
                confMediaType = ConfctrlConfMediatypeFlag.CONFCTRL_E_CONF_MEDIATYPE_FLAG_VOICE.getIndex();
                break;

            //视频
            case VIDEO_CONF:
                confMediaType = ConfctrlConfMediatypeFlag.CONFCTRL_E_CONF_MEDIATYPE_FLAG_VOICE.getIndex()
                        | ConfctrlConfMediatypeFlag.CONFCTRL_E_CONF_MEDIATYPE_FLAG_HDVIDEO.getIndex();
                break;

            //音频数据
            case VOICE_AND_DATA_CONF:
                confMediaType = ConfctrlConfMediatypeFlag.CONFCTRL_E_CONF_MEDIATYPE_FLAG_VOICE.getIndex()
                        | ConfctrlConfMediatypeFlag.CONFCTRL_E_CONF_MEDIATYPE_FLAG_DATA.getIndex();
                break;

            //视频数据
            case VIDEO_AND_DATA_CONF:
                confMediaType = ConfctrlConfMediatypeFlag.CONFCTRL_E_CONF_MEDIATYPE_FLAG_VOICE.getIndex()
                        | ConfctrlConfMediatypeFlag.CONFCTRL_E_CONF_MEDIATYPE_FLAG_VIDEO.getIndex()
                        | ConfctrlConfMediatypeFlag.CONFCTRL_E_CONF_MEDIATYPE_FLAG_DATA.getIndex();
                break;

            default:
                confMediaType = ConfctrlConfMediatypeFlag.CONFCTRL_E_CONF_MEDIATYPE_FLAG_BUTT.getIndex();
                break;
        }
        return  confMediaType;
    }

    /**
     * This method is used to convert conference media type.
     * @param confMediaType conference media type 会议媒体类型
     * @return
     */
    public static ConfConstant.ConfMediaType convertConfMediaType(int confMediaType)
    {
        ConfConstant.ConfMediaType mediaType = ConfConstant.ConfMediaType.VOICE_CONF;

        int voiceFlag = ConfctrlConfMediatypeFlag.CONFCTRL_E_CONF_MEDIATYPE_FLAG_VOICE.getIndex();
        int videoFlag = ConfctrlConfMediatypeFlag.CONFCTRL_E_CONF_MEDIATYPE_FLAG_VIDEO.getIndex();
        int hdVideoFlag = ConfctrlConfMediatypeFlag.CONFCTRL_E_CONF_MEDIATYPE_FLAG_HDVIDEO.getIndex();
        int dataFlag = ConfctrlConfMediatypeFlag.CONFCTRL_E_CONF_MEDIATYPE_FLAG_DATA.getIndex();
        //int telepresenceFlag = ConfctrlConfMediatypeFlag.CONFCTRL_E_CONF_MEDIATYPE_FLAG_VIDEO.getIndex();
        //int desktopSharingFlag = ConfctrlConfMediatypeFlag.CONFCTRL_E_CONF_MEDIATYPE_FLAG_DATA.getIndex();

        if (confMediaType == voiceFlag) {
            mediaType = ConfConstant.ConfMediaType.VOICE_CONF;
        }
        else if ((confMediaType == (voiceFlag | videoFlag)) || (confMediaType == (voiceFlag | hdVideoFlag))) {
            mediaType = ConfConstant.ConfMediaType.VIDEO_CONF;
        }
        else if (confMediaType == (voiceFlag | dataFlag)){
            mediaType = ConfConstant.ConfMediaType.VOICE_AND_DATA_CONF;
        }
        else if ((confMediaType == (voiceFlag | videoFlag | dataFlag)) || (confMediaType == (voiceFlag | hdVideoFlag | dataFlag))) {
            mediaType = ConfConstant.ConfMediaType.VIDEO_AND_DATA_CONF;
        }

        return  mediaType;
    }

    /**
     * This method is used to convert conference state
     * 转换会议状态
     * @param state
     * @return
     */
    public static ConfConstant.ConfConveneStatus convertConfctrlConfState(int state)
    {
        ConfConstant.ConfConveneStatus status = ConfConstant.ConfConveneStatus.UNKNOWN;
        switch (state)
        {
            case 0:
                status = ConfConstant.ConfConveneStatus.SCHEDULE;
                break;

            case 1:
                status = ConfConstant.ConfConveneStatus.CREATING;
                break;

            case 2:
                status = ConfConstant.ConfConveneStatus.GOING;
                break;

            case 3:
                status = ConfConstant.ConfConveneStatus.DESTROYED;
                break;

            default:
                break;
        }
        return status;
    }

    /**
     * This method is used to convert conference state
     * 转换会议状态
     * @param state
     * @return
     */
    public static ConfConstant.ConfConveneStatus convertConfctrlConfState(ConfctrlConfState state)
    {
        ConfConstant.ConfConveneStatus status = ConfConstant.ConfConveneStatus.UNKNOWN;
        switch (state)
        {
            case CONFCTRL_E_CONF_STATE_SCHEDULE:
                status = ConfConstant.ConfConveneStatus.SCHEDULE;
                break;

            case CONFCTRL_E_CONF_STATE_CREATING:
                status = ConfConstant.ConfConveneStatus.CREATING;
                break;

            case CONFCTRL_E_CONF_STATE_GOING:
                status = ConfConstant.ConfConveneStatus.GOING;
                break;

            case CONFCTRL_E_CONF_STATE_DESTROYED:
                status = ConfConstant.ConfConveneStatus.DESTROYED;
                break;

            default:
                break;
        }
        return status;
    }

    /**
     * This method is used to convert conference participant state
     * 转换与会者状态
     * @param state
     * @return
     */
    public static ConfConstant.ParticipantStatus convertConfctrlParticipantStatus(ConfctrlParticipantStatus state)
    {
        ConfConstant.ParticipantStatus status = ConfConstant.ParticipantStatus.UNKNOWN;
        switch (state)
        {
            case CONFCTRL_E_PARTICIPANT_STATUS_IN_CONF:
                status = ConfConstant.ParticipantStatus.IN_CONF;
                break;

            case CONFCTRL_E_PARTICIPANT_STATUS_CALLING:
                status = ConfConstant.ParticipantStatus.CALLING;
                break;

            case CONFCTRL_E_PARTICIPANT_STATUS_JOINING:
                status = ConfConstant.ParticipantStatus.JOINING;
                break;

            case CONFCTRL_E_PARTICIPANT_STATUS_LEAVED:
                status = ConfConstant.ParticipantStatus.LEAVED;
                break;

            case CONFCTRL_E_PARTICIPANT_STATUS_NO_EXIST:
                status = ConfConstant.ParticipantStatus.NO_EXIST;
                break;

            case CONFCTRL_E_PARTICIPANT_STATUS_BUSY:
                status = ConfConstant.ParticipantStatus.BUSY;
                break;

            case CONFCTRL_E_PARTICIPANT_STATUS_NO_ANSWER:
                status = ConfConstant.ParticipantStatus.NO_ANSWER;
                break;

            case CONFCTRL_E_PARTICIPANT_STATUS_REJECT:
                status = ConfConstant.ParticipantStatus.REJECT;
                break;

            case CONFCTRL_E_PARTICIPANT_STATUS_CALL_FAILED:
                status = ConfConstant.ParticipantStatus.CALL_FAILED;
                break;

            default:
                break;
        }
        return status;
    }

    public static List<TupConfECAttendeeInfo> convertMemberList(List<Member> memberList)
    {
        List<TupConfECAttendeeInfo> attendeeInfoList = new ArrayList<>();
        for (Member member : memberList)
        {
            TupConfECAttendeeInfo attendeeInfo = new TupConfECAttendeeInfo();

            attendeeInfo.setNumber(member.getNumber());
            attendeeInfo.setName(member.getDisplayName());
            attendeeInfo.setAcountId(member.getAccountId());
            attendeeInfo.setEmail(member.getEmail());
            attendeeInfo.setSms(member.getSms());
            attendeeInfo.setMute(member.isMute());

            ConfctrlConfRole role = ((member.getRole() == ConfConstant.ConfRole.CHAIRMAN) ?
                    ConfctrlConfRole.CONFCTRL_E_CONF_ROLE_CHAIRMAN : ConfctrlConfRole.CONFCTRL_E_CONF_ROLE_ATTENDEE);
            attendeeInfo.setRole(role);

            attendeeInfo.setType(ConfctrlAttendeeType.CONFCTRL_E_ATTENDEE_TYPE_NORMAL);

            memberList.add(member);
        }
        return attendeeInfoList;
    }


    /**
     * This method is used to convert member info
     * 转变与会者信息
     * @param attendeeInfo
     * @return
     */
    public static Member convertAttendeeInfo(TupConfECAttendeeInfo attendeeInfo)
    {
        Member member = new Member();

        member.setParticipantId(attendeeInfo.getParticipantId());
        member.setNumber(attendeeInfo.getNumber());
        member.setDisplayName(attendeeInfo.getName());
        member.setAccountId(attendeeInfo.getAcountId());
        member.setEmail(attendeeInfo.getEmail());
        member.setSms(attendeeInfo.getSms());
        member.setMute(attendeeInfo.isMute());
        member.setHandUp(attendeeInfo.isHandup());
        ConfConstant.ConfRole role = ((attendeeInfo.getRole() == ConfctrlConfRole.CONFCTRL_E_CONF_ROLE_CHAIRMAN) ?
                ConfConstant.ConfRole.CHAIRMAN : ConfConstant.ConfRole.ATTENDEE);
        member.setRole(role);
        member.setStatus(convertConfctrlParticipantStatus(attendeeInfo.getStatus()));
        member.setSelf((attendeeInfo.getIsSelf()==1));

        return member;
    }


    public static List<Member> convertAttendeeInfoList(List<TupConfECAttendeeInfo> attendeeInfoList)
    {
        List<Member> memberList = new ArrayList<>();
        for (TupConfECAttendeeInfo attendeeInfo : attendeeInfoList)
        {
            Member member = new Member();

            member.setNumber(attendeeInfo.getNumber());
            member.setDisplayName(attendeeInfo.getName());
            member.setAccountId(attendeeInfo.getAcountId());
            member.setEmail(attendeeInfo.getEmail());
            member.setSms(attendeeInfo.getSms());
            member.setMute(attendeeInfo.isMute());
            ConfConstant.ConfRole role = ((attendeeInfo.getRole() == ConfctrlConfRole.CONFCTRL_E_CONF_ROLE_CHAIRMAN) ?
                    ConfConstant.ConfRole.CHAIRMAN : ConfConstant.ConfRole.ATTENDEE);
            member.setRole(role);

            memberList.add(member);
        }
        return memberList;
    }

    /**
     * This method is used to Transform 'memberList' to 'ConfctrlAttendee'
     * @param memberList
     * @param isBookInstantConference
     * @return
     */
    public static List<ConfctrlAttendee> memberListToAttendeeList(List<Member> memberList, boolean isBookInstantConference)
    {
        List<ConfctrlAttendee> attendeeList = new ArrayList<>();
        for (Member member : memberList)
        {
            ConfctrlAttendee confctrlAttendee = new ConfctrlAttendee();
            confctrlAttendee.setNumber(member.getNumber());
            confctrlAttendee.setName(member.getDisplayName());
            confctrlAttendee.setEmail(member.getEmail());
            confctrlAttendee.setSms(member.getSms());
            confctrlAttendee.setAcountId(member.getAccountId());
            confctrlAttendee.setIsMute(member.isMute() ? 1 : 0);

            int isAutoInvite = 0;
            //预约延时会议均由与会者自主加入(此策略可根据实际需要修改)
            //立即会议中的普通与会者由服务器自动邀请，主席自主加入
            if (isBookInstantConference == true)
            {
                if (member.getRole() == ConfConstant.ConfRole.ATTENDEE)
                {
                    isAutoInvite = 1;
                }
            }
            confctrlAttendee.setIsAutoInvite(isAutoInvite);

            ConfctrlConfRole role = ((member.getRole() == ConfConstant.ConfRole.CHAIRMAN) ?
                    ConfctrlConfRole.CONFCTRL_E_CONF_ROLE_CHAIRMAN : ConfctrlConfRole.CONFCTRL_E_CONF_ROLE_ATTENDEE);
            confctrlAttendee.setRole(role);
            confctrlAttendee.setType(ConfctrlAttendeeType.CONFCTRL_E_ATTENDEE_TYPE_NORMAL);

            attendeeList.add(confctrlAttendee);
        }

        return attendeeList;
    }

}
