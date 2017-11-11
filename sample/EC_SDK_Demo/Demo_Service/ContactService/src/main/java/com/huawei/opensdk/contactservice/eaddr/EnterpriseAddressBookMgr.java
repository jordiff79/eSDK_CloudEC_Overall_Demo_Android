package com.huawei.opensdk.contactservice.eaddr;

import android.util.Log;

import com.huawei.opensdk.sdkwrapper.manager.TupMgr;
import com.huawei.tup.eaddr.TupEaddrContactorInfo;
import com.huawei.tup.eaddr.TupEaddrContactorSearchItem;
import com.huawei.tup.eaddr.TupEaddrContactorSearchRst;
import com.huawei.tup.eaddr.TupEaddrDefIconSetItem;
import com.huawei.tup.eaddr.TupEaddrDeptSearchRst;
import com.huawei.tup.eaddr.TupEaddrIconSearchItem;
import com.huawei.tup.eaddr.TupEaddrIconSearchRst;
import com.huawei.tup.eaddr.TupEaddrManager;
import com.huawei.tup.eaddr.TupEaddrNotify;
import com.huawei.tup.eaddr.TupEaddrSetDefIconRst;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is about enterprise address book manager.
 * 通讯录模块管理类
 */
public class EnterpriseAddressBookMgr implements TupEaddrNotify {

    private static final String TAG = EnterpriseAddressBookMgr.class.getSimpleName();

    /**
     * The EnterpriseAddressBookMgr function object.
     * EnterpriseAddressBookMgr对象
     */
    private static EnterpriseAddressBookMgr instance;

    /**
     * UI notification.
     * 回调接口对象
     */
    private static IEntAddressBookNotification notification;

    /**
     * The TupEaddrManager object.
     * TupEaddrManager对象
     */
    private TupEaddrManager tupEaddrManager;

    /**
     * Query the contact's serial number
     * 查询联系人的序列号
     */
    private int queryContactsInfoSeq;

    /**
     * Query the serial number of the contact avatar
     * 查询联系人头像的序列号
     */
    private int queryContactsIconSeq;

    /**
     * Query the contact department's serial number
     * 查询联系人部门的序列号
     */
    private int queryDepartmentSeq;

    /**
     * Query the serial number of your own information
     * 查询自己信息的序列号
     */
    private int querySelfSeq;

    /**
     * Map collection When querying a user's avatar
     * 查询用户头像时的map集合
     */
    private static Map<Integer, String>querySeqAccountMap = new HashMap<>();

    /**
     * List of contacts that are queried
     * 查询到的联系人列表
     */
    private List<TupEaddrContactorInfo> list;

    /**
     * This method is used to get contacts list
     * 获取查询到的联系人列表
     * @return List<TupEaddrContactorInfo> Return the list of contacts that are queried
     *                                     返回查询到的联系人列表
     */
    public List<TupEaddrContactorInfo> getList()
    {
        return list;
    }

    /**
     * This is a constructor of this class
     * 构造函数
     */
    public EnterpriseAddressBookMgr() {
        tupEaddrManager = TupMgr.getInstance().getEaddrManagerIns();
        queryContactsInfoSeq = 1;
        queryContactsIconSeq = 1;
        queryDepartmentSeq = 1;
    }

    /**
     * This method is used to get instance object of EnterpriseAddressBookMgr.
     * 获取EnterpriseAddressBookMgr对象实例
     * @return EnterpriseAddressBookMgr Return instance object of EnterpriseAddressBookMgr
     *                                  返回一个对象的示例
     */
    public static EnterpriseAddressBookMgr getInstance()
    {
        if (instance == null) {
            instance = new EnterpriseAddressBookMgr();
        }
        return instance;
    }

    /**
     * This method is used to register EnterpriseAddressBookMgr module UI callback.
     * 注册回调
     * @param notification
     */
    public void registerNotification(IEntAddressBookNotification notification)
    {
        this.notification = notification;
    }

    /**
     * This method is used to search self's information.
     * 获取自己的信息
     * @param keyWords Indicates keyWords
     *                 搜索条件
     * @return int Return seq
     *             返回查询的序列号
     */
    public int searchSelfInfo(String keyWords)
    {
        querySelfSeq = searchContacts(keyWords);
        return querySelfSeq;
    }

    /**
     * This method is used to search contact's information.
     * 获取联系人的信息
     * @param keyWords Indicates keyWords
     *                 搜索条件
     * @return int Return seq
     *             返回查询的序列号
     */
    public int searchContacts(String keyWords)
    {
        tupEaddrManager = TupMgr.getInstance().getEaddrManagerIns();
        int seq = queryContactsInfoSeq++;
        if (null == keyWords)
        {
            Log.e(TAG, "Search condition is empty");
        }
        TupEaddrContactorSearchItem contactorSearchItem = new TupEaddrContactorSearchItem();
        contactorSearchItem.setDepId("");
        contactorSearchItem.setExactSearch(0);
        contactorSearchItem.setPageIndex(1);
        contactorSearchItem.setSearchItem(keyWords);
        contactorSearchItem.setSeqNo(seq);

        int ret = tupEaddrManager.searchContactor(contactorSearchItem);
        Log.i(TAG, "searchResult -->" + ret);
        return seq;
    }

    /**
     * This method is used to get user's icon.
     * 获取用户头像
     * @param account Indicates account.
     *                用户账号
     * @return int Return the result of getting user's icon. If success return 0, otherwise return corresponding error code
     *             返回获取用户头像的结果，取值；成功返回0，失败返回相应错误码
     */
    public int getUserIcon(String account)
    {
        int seq = queryContactsIconSeq++;
        TupEaddrIconSearchItem iconSearchItem = new TupEaddrIconSearchItem();
        iconSearchItem.setEnMsgPrio(TupEaddrIconSearchItem.VTOP_MSG_PRIO_HIGH);
        iconSearchItem.setSeqNo(seq);
        iconSearchItem.setStaffAccount(account);
        querySeqAccountMap.put(seq, account);
        int ret = tupEaddrManager.getIcon(iconSearchItem);
        if (ret != 0)
        {
            Log.e(TAG, "search user icon Result -->" + ret);
        }
        return seq;
    }

    /**
     * This method is used to search department structure.
     * 搜索部门结构
     * @param departmentId
     * @return
     */
    public int searchDepartment(String  departmentId)
    {
        int reqNo = queryDepartmentSeq++;
        return reqNo;
    }

    /**
     * This method is used to set system icon.
     * 设置系统头像
     * @param resId 头像id
     * @return int Return the result of setting system icon. If success return 0,otherwise return corresponding error code
     *             返回设置系统头像的结果，取值；成功返回0，失败返回相应的错误码
     */
    public int setSystemIcon(int resId)
    {
        int result = tupEaddrManager.setSysIcon(resId);
        if (result != 0)
        {
            Log.e(TAG, "Set user system icon filed, result -->" + result);
        }
        return result;
    }

    /**
     * This method is used to set self-defined icon.
     * 设置自定义头像
     * @param smallIconFilePath  Indicates the small icon file path
     *                           小尺寸头像路径
     * @param mediumIconFilePath Indicates the medium icon file path
     *                           中等尺寸头像路径
     * @param largeIconFilePath  Indicates the large icon file path
     *                           大尺寸头像路径
     * @return int Return the result of setting system icon. If success return 0,otherwise return corresponding error code
     *             返回设置自定义头像的结果，取值；成功返回0，失败返回相应的错误码
     */
    public int setDefinedIcon(String smallIconFilePath, String mediumIconFilePath, String largeIconFilePath)
    {
        TupEaddrDefIconSetItem defIconSetItem = new TupEaddrDefIconSetItem(largeIconFilePath, mediumIconFilePath, smallIconFilePath);
        TupEaddrSetDefIconRst setIconResult = tupEaddrManager.setDefIcon(defIconSetItem);
        if (setIconResult.getRst() != 0)
        {
            Log.e(TAG, "User set defined icon failed, result -->" + setIconResult.getRst());
        }
        else
        {
            Log.i(TAG, "User setDefIcon result = " + setIconResult.getRst() + ", ModifyTime = " + setIconResult.getModifyTime());
        }
        return setIconResult.getRst();
    }

    /**
     * This is a callback function to handle the searching contact result.
     * 处理搜索联系人信息返回结果的回调
     * @param tupEaddrContactorSearchRst Indicates searching contact result
     *                                   搜索联系人信息返回结果的对象
     */
    @Override
    public void onSearchContactResult(TupEaddrContactorSearchRst tupEaddrContactorSearchRst) {

        //获取序列号-->和调用查询方法返回的序列号相一致
        int seqNo = tupEaddrContactorSearchRst.getSeqNo();
        int result = tupEaddrContactorSearchRst.getRet();

        //获取联系人成功返回0
        if (result == 0)
        {
            //获取查询到的联系人列表以及查询到的联系人总数
            List<TupEaddrContactorInfo> contactorInfos = tupEaddrContactorSearchRst.getContactorInfoList();
            int totalNum = tupEaddrContactorSearchRst.getTotalNum();
            //查询到0个联系人
            if (0 == totalNum)
            {
                notification.onEntAddressBookNotify(EntAddressBookConstant.Event.SEARCH_CONTACTS_NOT_FOUND, null);
            }
            //查询的登陆的用户信息
            else if (querySelfSeq == seqNo)
            {
                notification.onEntAddressBookNotify(EntAddressBookConstant.Event.SEARCH_SELF_COMPLETE, contactorInfos);
            }
            //其余查询结果
            else
            {
                list = contactorInfos;
                QueryContactsInfoResult queryContactsResult = new QueryContactsInfoResult();
                queryContactsResult.setQuerySeq(seqNo);
                List<EntAddressBookInfo> contactsList = new ArrayList<>();
                for (TupEaddrContactorInfo contactorInfo : contactorInfos)
                {
                    EntAddressBookInfo entAddressBookInfo = new EntAddressBookInfo();
                    entAddressBookInfo.setEaddrAccount(contactorInfo.getStaffAccount());
                    if(contactorInfo.getStaffAccount().isEmpty() || contactorInfo.getStaffAccount() == null)
                    {
                        break;
                    }
                    EnterpriseAddressBookMgr.getInstance().getUserIcon(contactorInfo.getStaffAccount()); //查询用户头像
                    entAddressBookInfo.setTerminal(contactorInfo.getTerminal());
                    entAddressBookInfo.setEaddrDept(contactorInfo.getDeptName());
                    entAddressBookInfo.setSysIconID(10);
                    contactsList.add(entAddressBookInfo);
                }
                queryContactsResult.setList(contactsList);
                notification.onEntAddressBookNotify(EntAddressBookConstant.Event.SEARCH_CONTACTS_COMPLETE, queryContactsResult);
            }
            Log.i(TAG, tupEaddrContactorSearchRst.getTotalNum() + "Get the total number of returned contacts");
        }
        else
        {
            Log.e(TAG, "Search contacts failed, result -->" + result);
            notification.onEntAddressBookNotify(EntAddressBookConstant.Event.SEARCH_CONTACTS_FAILED, null);
        }

    }

    /**
     * This is a callback function to handle the getting user's icon result.
     * 处理获取用户头像返回结果的回调
     * @param tupEaddrIconSearchRst Indicates getting user's icon result
     *                              获取用户头像返回结果对象
     */
    @Override
    public void onGetIconResult(TupEaddrIconSearchRst tupEaddrIconSearchRst) {

        int result = tupEaddrIconSearchRst.getRet();
        int seqNo = tupEaddrIconSearchRst.getSeqNo();

        //获取到某个用户的头像
        String account = querySeqAccountMap.get(seqNo);

        //获取头像成功返回0
        if (result == 0)
        {
            int sysId = tupEaddrIconSearchRst.getSysAvatarID();
            String avatarFile = tupEaddrIconSearchRst.getAvatarFile();

            //获取到的是系统头像
            if (sysId >= 0 && avatarFile.isEmpty())
            {
                EntAddressBookIconInfo iconInfo = new EntAddressBookIconInfo();
                iconInfo.setAccount(account);
                iconInfo.setIconId(sysId);
                iconInfo.setIconSeq(seqNo);
                notification.onEntAddressBookIconNotify(EntAddressBookConstant.Event.GET_CONTACTS_SYSTEM_ICON, iconInfo);
            }
            //获取到的是自定义头像
            else
            {
                EntAddressBookIconInfo iconInfo = new EntAddressBookIconInfo();
                iconInfo.setAccount(account);
                iconInfo.setIconFile(avatarFile);
                iconInfo.setIconSeq(seqNo);
                notification.onEntAddressBookIconNotify(EntAddressBookConstant.Event.GET_CONTACTS_CUSTOM_ICON, iconInfo);
            }
            Log.i(TAG, sysId + "System Avatar ID  " + avatarFile + "Custom Avatar filename");
        }
        else
        {
            //获取头像失败
            EntAddressBookIconInfo iconInfo = new EntAddressBookIconInfo();
            iconInfo.setAccount(account);
            iconInfo.setIconSeq(seqNo);
            Log.e(TAG, "User get icon failed, result -->" + result);
            notification.onEntAddressBookIconNotify(EntAddressBookConstant.Event.GET_CONTACTS_ICON_FAILED, iconInfo);
        }

    }

    /**
     * This is a callback function to handle the searching department result.
     * 处理搜索部门结构返回结果的回调
     * @param tupEaddrDeptSearchRst Indicates searching department result.
     *                              搜索部门结构返回结果对象
     */
    @Override
    public void onSearchDepartmentResult(TupEaddrDeptSearchRst tupEaddrDeptSearchRst) {

    }
}
