package com.huawei.opensdk.ec_sdk_demo.ui.eaddrbook;

import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.huawei.opensdk.commonservice.localbroadcast.CustomBroadcastConstants;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcast;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcastReceiver;
import com.huawei.opensdk.contactservice.eaddr.EntAddressBookIconInfo;
import com.huawei.opensdk.contactservice.eaddr.EntAddressBookInfo;
import com.huawei.opensdk.contactservice.eaddr.EnterpriseAddressBookMgr;
import com.huawei.opensdk.contactservice.eaddr.QueryContactsInfoResult;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.adapter.EnterpriseListAdapter;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;
import com.huawei.opensdk.ec_sdk_demo.ui.IntentConstant;
import com.huawei.opensdk.ec_sdk_demo.ui.base.BaseActivity;
import com.huawei.opensdk.ec_sdk_demo.util.ActivityUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is about search contacts activity.
 */
public class EnterpriseAddrBookActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener, LocBroadcastReceiver {

    private ImageView eaddrBack = null;
    private EditText eaddrKeys = null;
    private ImageView eaddrSearch = null;
    private ListView eaddrList = null;
    private EnterpriseListAdapter enterpriseListAdapter;

    private static List<EntAddressBookInfo> list = new ArrayList<>();
    public static List<EntAddressBookInfo> getList() {
        return list;
    }

    private static int eaddrIndex = 0;
    public static int getEaddrIndex() {
        return eaddrIndex;
    }

    private String mIconPath;
    private int mIconId;
    private int searchSeq;
    private int[] sysIcon = EnterpriseAddrTools.getSystemIcon();  //System Avatar ID

    private String[] eActions = new String[]{
            CustomBroadcastConstants.ACTION_ENTERPRISE_GET_CONTACT_RESULT,
            CustomBroadcastConstants.ACTION_ENTERPRISE_GET_CONTACT_NULL,
            CustomBroadcastConstants.ACTION_ENTERPRISE_GET_CONTACT_FAILED,
            CustomBroadcastConstants.ACTION_ENTERPRISE_GET_HEAD_DEF_PHOTO,
            CustomBroadcastConstants.ACTION_ENTERPRISE_GET_HEAD_PHOTO_FAILED,
            CustomBroadcastConstants.ACTION_ENTERPRISE_GET_HEAD_SYS_PHOTO
    };

    @Override
    public void initializeComposition() {
        setContentView(R.layout.activity_enterprise_addr_book);
        eaddrBack = (ImageView)findViewById(R.id.book_back);
        eaddrKeys = (EditText)findViewById(R.id.book_keys);
        eaddrSearch = (ImageView)findViewById(R.id.book_right);
        eaddrList = (ListView)findViewById(R.id.search_list);

        enterpriseListAdapter = new EnterpriseListAdapter(this,list);
        eaddrList.setAdapter(enterpriseListAdapter);

        eaddrBack.setOnClickListener(this);
        eaddrSearch.setOnClickListener(this);
        eaddrList.setOnItemClickListener(this);
    }

    @Override
    public void initializeData() {

    }

    @Override
    protected void onResume() {
        LocBroadcast.getInstance().registerBroadcast(this, eActions);
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.book_back:
                list.clear();
                LocBroadcast.getInstance().unRegisterBroadcast(this, eActions);
                finish();
                break;
            case R.id.book_right:
                String keywords = eaddrKeys.getText().toString();
                if(null == keywords || keywords.isEmpty())
                {
                    Toast.makeText(EnterpriseAddrBookActivity.this,"Search content can not be empty!",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if (list.size() == 0)
                    {
                        searchSeq = EnterpriseAddressBookMgr.getInstance().searchContacts(keywords);
                    }
                    else
                    {
                        list.clear();
                        searchSeq = EnterpriseAddressBookMgr.getInstance().searchContacts(keywords);
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        eaddrIndex = position;
        Intent intent = new Intent(IntentConstant.EADDR_INFO_ACTIVITY_ACTION);
        ActivityUtil.startActivity(this, intent);
    }

    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case UIConstants.ENTERPRISE_SEARCH_SUCCESS:
                    QueryContactsInfoResult contactsInfoResult = (QueryContactsInfoResult) msg.obj;
                    if (contactsInfoResult.getQuerySeq() == searchSeq)
                    {
                        list = contactsInfoResult.getList();
                    }
                    enterpriseListAdapter.notifyDataSetChanged(list);
                    break;
                case UIConstants.ENTERPRISE_SEARCH_NULL:
                    list.clear();
                    enterpriseListAdapter.notifyDataSetChanged(list);
                    Toast.makeText(EnterpriseAddrBookActivity.this,"There is no inquiry to the contact!",Toast.LENGTH_SHORT).show();
                    eaddrKeys.setText("");
                    break;
                case UIConstants.ENTERPRISE_SEARCH_FAILED:
                    Toast.makeText(EnterpriseAddrBookActivity.this,"Search contact failed!",Toast.LENGTH_SHORT).show();
                case UIConstants.ENTERPRISE_HEAD_SYS:
                    if (msg.obj instanceof EntAddressBookIconInfo)
                    {
                        EntAddressBookIconInfo iconInfo = (EntAddressBookIconInfo) msg.obj;
                        for (int i = 0; i < list.size(); i++)
                        {
                            if (list.get(i).getEaddrAccount().equals(iconInfo.getAccount()) && iconInfo.getIconId() >= 0)
                            {
                                mIconId = iconInfo.getIconId();
                                list.get(i).setSysIconID(sysIcon[mIconId]);
                                break;
                            }
                        }
                        enterpriseListAdapter.notifyDataSetChanged(list);
                    }
                    break;
                case UIConstants.ENTERPRISE_HEAD_DEF:
                    if (msg.obj instanceof EntAddressBookIconInfo)
                    {
                        EntAddressBookIconInfo defIconInfo = (EntAddressBookIconInfo) msg.obj;
                        String defIcon = defIconInfo.getIconFile();
                        for (int j = 0; j < list.size(); j++)
                        {
                            if (list.get(j).getEaddrAccount().equals(defIconInfo.getAccount()) && defIcon != "")
                            {
                                mIconPath = Environment.getExternalStorageDirectory() + File.separator + "tupcontact" + File.separator + "icon" + File.separator + defIcon;
                                list.get(j).setHeadIconPath(mIconPath);
                                break;
                            }
                        }
                        enterpriseListAdapter.notifyDataSetChanged(list);
                    }
                    break;
                case UIConstants.ENTERPRISE_HEAD_NULL:
                    EntAddressBookIconInfo defIconInfo = (EntAddressBookIconInfo) msg.obj;
                    for (int w = 0; w < list.size(); w++)
                    {
                        if (list.get(w).getEaddrAccount().equals(defIconInfo.getAccount()))
                        {
                            list.get(w).setSysIconID(10);
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onReceive(String broadcastName, Object obj) {
        switch (broadcastName)
        {
            case CustomBroadcastConstants.ACTION_ENTERPRISE_GET_CONTACT_RESULT:
                Message msgContactSuccess = handler.obtainMessage(UIConstants.ENTERPRISE_SEARCH_SUCCESS, obj);
                handler.sendMessage(msgContactSuccess);
                break;
            case CustomBroadcastConstants.ACTION_ENTERPRISE_GET_CONTACT_NULL:
                Message msgContactNull = handler.obtainMessage(UIConstants.ENTERPRISE_SEARCH_NULL, obj);
                handler.sendMessage(msgContactNull);
                break;
            case CustomBroadcastConstants.ACTION_ENTERPRISE_GET_CONTACT_FAILED:
                Message msgContactFailed = handler.obtainMessage(UIConstants.ENTERPRISE_SEARCH_FAILED, obj);
                handler.sendMessage(msgContactFailed);
                break;
            case CustomBroadcastConstants.ACTION_ENTERPRISE_GET_HEAD_SYS_PHOTO:
                Message msgSys = handler.obtainMessage(UIConstants.ENTERPRISE_HEAD_SYS, obj);
                handler.sendMessage(msgSys);
                break;
            case CustomBroadcastConstants.ACTION_ENTERPRISE_GET_HEAD_DEF_PHOTO:
                Message msgDef = handler.obtainMessage(UIConstants.ENTERPRISE_HEAD_DEF, obj);
                handler.sendMessage(msgDef);
                break;
            case CustomBroadcastConstants.ACTION_ENTERPRISE_GET_HEAD_PHOTO_FAILED:
                Message msgFailed = handler.obtainMessage(UIConstants.ENTERPRISE_HEAD_NULL, obj);
                handler.sendMessage(msgFailed);
                break;
            default:
                break;
        }
    }
}
