package com.huawei.opensdk.ec_sdk_demo.ui.login;

import android.app.Activity;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.ui.base.BaseActivity;
import com.huawei.opensdk.loginmgr.LoginConstant;

public class LoginSettingActivity extends BaseActivity implements View.OnClickListener
{
    private EditText mRegServerEditText;
    private EditText mServerPortEditText;
    private CheckBox mVpnCheckBox;
    private String mRegServerAddress;
    private String mServerPort;
    private boolean mIsVpn;
    private SharedPreferences mSharedPreferences;

    private void initView()
    {
        mRegServerEditText = (EditText) findViewById(R.id.et_register_server_address);
        mServerPortEditText = (EditText) findViewById(R.id.et_server_port);
        mVpnCheckBox = (CheckBox) findViewById(R.id.check_vpn_connect);
        ImageView searchButton = (ImageView) findViewById(R.id.right_img);
        ImageView navImage = (ImageView) findViewById(R.id.nav_iv);
        searchButton.setVisibility(View.GONE);
        TextView rightButton = (TextView) findViewById(R.id.right_btn);
        rightButton.setVisibility(View.VISIBLE);
        rightButton.setText(getString(R.string.save));

        rightButton.setOnClickListener(this);
        navImage.setOnClickListener(this);

        mVpnCheckBox.setChecked(mSharedPreferences.getBoolean(LoginConstant.TUP_VPN, false));
        mRegServerEditText.setText(mSharedPreferences.getString(LoginConstant.TUP_REGSERVER, LoginConstant.BLANK_STRING));
        mServerPortEditText.setText(mSharedPreferences.getString(LoginConstant.TUP_PORT, LoginConstant.BLANK_STRING));
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.right_btn:
                mRegServerAddress = mRegServerEditText.getText().toString().trim();
                mServerPort = mServerPortEditText.getText().toString().trim();
                mIsVpn = mVpnCheckBox.isChecked();
                saveLoginSetting(mIsVpn, mRegServerAddress, mServerPort);
                showToast(R.string.save_success);
                finish();
                break;
            case R.id.check_vpn_connect:
                if (mVpnCheckBox.isChecked())
                {
                    mVpnCheckBox.setChecked(true);
                }
                else
                {
                    mVpnCheckBox.setChecked(false);
                }
                break;
            case R.id.nav_iv:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void initializeComposition()
    {
        setContentView(R.layout.activity_login_setting);
        initView();
        mVpnCheckBox.setOnClickListener(this);
    }

    @Override
    public void initializeData()
    {
        mSharedPreferences = getSharedPreferences(LoginConstant.FILE_NAME, Activity.MODE_PRIVATE);
    }

    private void saveLoginSetting(boolean isVpn, String regServerAddress, String serverPort)
    {
        if (TextUtils.isEmpty(regServerAddress) || TextUtils.isEmpty(serverPort))
        {
            showToast(R.string.server_information_not_empty);
            return;
        }
        mSharedPreferences.edit().putBoolean(LoginConstant.TUP_VPN, isVpn)
                .putString(LoginConstant.TUP_REGSERVER, regServerAddress)
                .putString(LoginConstant.TUP_PORT, serverPort)
                .commit();
    }
}
