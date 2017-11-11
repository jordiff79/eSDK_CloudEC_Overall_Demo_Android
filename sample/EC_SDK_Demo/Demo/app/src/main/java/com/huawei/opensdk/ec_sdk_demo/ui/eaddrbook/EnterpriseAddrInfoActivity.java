package com.huawei.opensdk.ec_sdk_demo.ui.eaddrbook;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.huawei.opensdk.contactservice.eaddr.EntAddressBookInfo;
import com.huawei.opensdk.contactservice.eaddr.EnterpriseAddressBookMgr;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.ui.base.BaseActivity;
import com.huawei.tup.eaddr.TupEaddrContactorInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is about contact info activity.
 */
public class EnterpriseAddrInfoActivity extends BaseActivity implements View.OnClickListener {

    private ImageView userAvatar = null;
    private TextView account = null;
    private TextView name = null;
    private TextView staff = null;
    private TextView number = null;
    private TextView dept = null;
    private TextView title = null;
    private TextView mobile = null;
    private TextView phone = null;
    private TextView homePhone = null;
    private TextView email = null;
    private TextView otherPhone = null;
    private TextView otherPhone2 = null;
    private TextView zip = null;
    private TextView address = null;
    private TextView signature = null;
    private ImageView blogSex = null;
    private ImageView eaddrBack = null;

    private List<TupEaddrContactorInfo> listEaddrs = new ArrayList<>();
    private List<EntAddressBookInfo> iconEaddrs = new ArrayList<>();
    private int index;

    @Override
    public void initializeComposition() {
        setContentView(R.layout.activity_enterprise_info);

        //Get contact list (all contact information except Avatar)
        listEaddrs = EnterpriseAddressBookMgr.getInstance().getList();

        //Get Contact Avatar
        iconEaddrs = EnterpriseAddrBookActivity.getList();

        //Get clicked contact position and list.index() correspondence
        index = EnterpriseAddrBookActivity.getEaddrIndex();

        //Set User avatar
        userAvatar = (ImageView)findViewById(R.id.eaddr_head_iv);
        int iconId = iconEaddrs.get(index).getSysIconID();
        String iconPath = iconEaddrs.get(index).getHeadIconPath();
        if (!iconEaddrs.get(index).getHeadIconPath().isEmpty())
        {
            Bitmap headIcon = EnterpriseAddrTools.getBitmapByPath(iconPath);
            userAvatar.setImageBitmap(headIcon);
        }
        else
        {
            userAvatar.setImageResource(iconId);
        }

        account = (TextView)findViewById(R.id.staff_account);
        name = (TextView)findViewById(R.id.eaddr_name_tv);
        staff = (TextView)findViewById(R.id.staff_no);
        number = (TextView)findViewById(R.id.terminal);
        dept = (TextView)findViewById(R.id.dept_name);
        title = (TextView)findViewById(R.id.title);
        mobile = (TextView)findViewById(R.id.mobile);
        phone = (TextView)findViewById(R.id.office_phone);
        homePhone = (TextView)findViewById(R.id.home_phone);
        email = (TextView)findViewById(R.id.email);
        otherPhone = (TextView)findViewById(R.id.other_phone);
        otherPhone2 = (TextView)findViewById(R.id.other_phone2);
        zip = (TextView)findViewById(R.id.zip_code);
        address = (TextView)findViewById(R.id.address);
        signature = (TextView)findViewById(R.id.eaddr_signature_tv);
        blogSex = (ImageView)findViewById(R.id.eaddr_sex_iv);

        eaddrBack = (ImageView)findViewById(R.id.book_back);
        eaddrBack.setOnClickListener(this);

        account.setText(listEaddrs.get(index).getStaffAccount());
        name.setText(listEaddrs.get(index).getPersonName());
        staff.setText(listEaddrs.get(index).getStaffno());
        number.setText(listEaddrs.get(index).getTerminal());
        dept.setText(listEaddrs.get(index).getDeptName());
        title.setText(listEaddrs.get(index).getTitle());
        mobile.setText(listEaddrs.get(index).getMobile());
        phone.setText(listEaddrs.get(index).getOfficephone());
        homePhone.setText(listEaddrs.get(index).getHomephone());
        email.setText(listEaddrs.get(index).getEmail());
        otherPhone.setText(listEaddrs.get(index).getOtherphone());
        otherPhone2.setText(listEaddrs.get(index).getOtherphone2());
        zip.setText(listEaddrs.get(index).getZipCode());
        address.setText(listEaddrs.get(index).getAddress());
        signature.setText(listEaddrs.get(index).getSignature());
        if(listEaddrs.get(index).getGender().equals("female"))
        {
            blogSex.setBackgroundResource(R.drawable.sex_female);
        }
        else if(listEaddrs.get(index).getGender().equals("male"))
        {
            blogSex.setBackgroundResource(R.drawable.sex_male);
        }
    }

    @Override
    public void initializeData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.book_back:
                finish();
                break;
        }
    }
}
