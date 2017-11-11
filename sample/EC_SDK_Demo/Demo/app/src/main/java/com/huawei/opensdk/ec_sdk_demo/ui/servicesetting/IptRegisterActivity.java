package com.huawei.opensdk.ec_sdk_demo.ui.servicesetting;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.huawei.opensdk.callmgr.iptService.IIptNotification;
import com.huawei.opensdk.callmgr.iptService.IptMgr;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.ui.base.BaseActivity;

import common.TupCallParam;


public class IptRegisterActivity extends BaseActivity implements IIptNotification, View.OnClickListener {

    private ProgressDialog mDialog;

    private ImageView imageButton_dnd;
    private ImageView imageButton_cw;
    private ImageView imageButton_cfu;
    private ImageView imageButton_cfb;
    private ImageView imageButton_cfna;
    private ImageView imageButton_cfnr;

    private EditText et_cfu_num;
    private EditText et_cfb_num;
    private EditText et_cfna_num;
    private EditText et_cfnr_num;

    @Override
    public void initializeComposition() {
        setContentView(R.layout.ipt_service_setting);
        initView();
        showIPT();
    }

    @Override
    public void initializeData() {

    }

    protected void initView() {
        findViewById(R.id.imageButton_dnd).setOnClickListener(this);
        imageButton_dnd = (ImageView) findViewById(R.id.imageButton_dnd);

        findViewById(R.id.imageButton_cw).setOnClickListener(this);
        imageButton_cw = (ImageView) findViewById(R.id.imageButton_cw);

        findViewById(R.id.imageButton_cfu).setOnClickListener(this);
        imageButton_cfu = (ImageView) findViewById(R.id.imageButton_cfu);

        findViewById(R.id.imageButton_cfb).setOnClickListener(this);
        imageButton_cfb = (ImageView) findViewById(R.id.imageButton_cfb);

        findViewById(R.id.imageButton_cfna).setOnClickListener(this);
        imageButton_cfna = (ImageView) findViewById(R.id.imageButton_cfna);

        findViewById(R.id.imageButton_cfnr).setOnClickListener(this);
        imageButton_cfnr = (ImageView) findViewById(R.id.imageButton_cfnr);

        et_cfu_num = (EditText) findViewById(R.id.et_cfu_num);
        et_cfb_num = (EditText) findViewById(R.id.et_cfb_num);
        et_cfna_num = (EditText) findViewById(R.id.et_cfna_num);
        et_cfnr_num = (EditText) findViewById(R.id.et_cfnr_num);

    }

    public void showIPT()
    {
        boolean right;
        boolean register;
         {
            right = IptMgr.getInstance().getDndRegisterInfo().getRight() == 1 ? true : false;
            register = IptMgr.getInstance().getDndRegisterInfo().getRegister() == 1 ? true : false;
            if (register && right) {
                imageButton_dnd.setImageResource(R.drawable.setting_switch_on);
                imageButton_dnd.setTag("Register");
            } else if(!right){
                imageButton_dnd.setTag("false");
            }
        }

        {
            right = IptMgr.getInstance().getCwRegisterInfo().getRight() == 1 ? true : false;
            register = IptMgr.getInstance().getCwRegisterInfo().getRegister() == 1 ? true : false;
            if (register && right) {
                imageButton_cw.setImageResource(R.drawable.setting_switch_on);
                imageButton_cw.setTag("Register");
            } else if(!right){
                imageButton_cw.setTag("false");
            }
        }

        {
            right = IptMgr.getInstance().getCfuRegisterInfo().getRight() == 1 ? true : false;
            register = IptMgr.getInstance().getCfuRegisterInfo().getRegister() == 1 ? true : false;
            if (register && right) {
                imageButton_cfu.setImageResource(R.drawable.setting_switch_on);
                imageButton_cfu.setTag("Register");
                et_cfu_num.setEnabled(false);
            } else if(!right){
                imageButton_cfu.setTag("false");
            }else if (right)
            {
                imageButton_cfu.setTag("first");
            }
        }

        {
            right = IptMgr.getInstance().getCfbRegisterInfo().getRight() == 1 ? true : false;
            register = IptMgr.getInstance().getCfbRegisterInfo().getRegister() == 1 ? true : false;
            if (register && right) {
                imageButton_cfb.setImageResource(R.drawable.setting_switch_on);
                imageButton_cfb.setTag("Register");
                et_cfb_num.setEnabled(false);
            } else if(!right){
                imageButton_cfb.setTag("false");
            }else if (right)
            {
                imageButton_cfb.setTag("first");
            }
        }

        {
            right = IptMgr.getInstance().getCfnaRegisterInfo().getRight() == 1 ? true : false;
            register = IptMgr.getInstance().getCfnaRegisterInfo().getRegister() == 1 ? true : false;
            if (register && right) {
                imageButton_cfna.setImageResource(R.drawable.setting_switch_on);
                imageButton_cfna.setTag("Register");
                et_cfna_num.setEnabled(false);
            } else if(!right){
                imageButton_cfna.setTag("false");
            }else if (right)
            {
                imageButton_cfna.setTag("first");
            }
        }

        {
            right = IptMgr.getInstance().getCfnrRegisterInfo().getRight() == 1 ? true : false;
            register = IptMgr.getInstance().getCfnrRegisterInfo().getRegister() == 1 ? true : false;
            if (register && right) {
                imageButton_cfnr.setImageResource(R.drawable.setting_switch_on);
                imageButton_cfnr.setTag("Register");
                et_cfnr_num.setEnabled(false);
            } else if(!right){
                imageButton_cfnr.setTag("false");
            }else if (right)
            {
                imageButton_cfnr.setTag("first");
            }
        }
    }

    @Override
    public void onSetIptServiceSuc(int onSetIptServiceSuc) {
        dismissLoginDialog();

        Message message = Message.obtain();
        message.what = onSetIptServiceSuc;
        message.obj = "onSetIptServiceSuc";
        handler.sendMessage(message);

    }

    @Override
    public void onSetIptServiceFal(int onSetIptServiceSuc) {
        dismissLoginDialog();

        Message message = Message.obtain();
        message.what = onSetIptServiceSuc;
        message.obj = "onSetIptServiceFal";
        handler.sendMessage(message);

    }

    @Override
    protected void onResume() {
        super.onResume();
        IptMgr.getInstance().regIptNotification(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IptMgr.getInstance().unregIptNotification(this);
    }

    @Override
    public void onClick(View v) {
        String dataNumber = "";
        final ImageView button = (ImageView) v;
        int id = v.getId();
        final int iptServiceType;
        switch (id) {
            case R.id.imageButton_dnd:
                if(!"false".equals(button.getTag()))
                {
                    showLoginDialog("ipt");
                    if ("Register".equals(button.getTag())) {
                        button.setTag("");
                        iptServiceType = TupCallParam.CALL_E_SERVICE_CALL_TYPE.CALL_E_SERVICE_CALL_TYPE_UNREG_DND;
                    } else {
                        button.setTag("Register");
                        iptServiceType = TupCallParam.CALL_E_SERVICE_CALL_TYPE.CALL_E_SERVICE_CALL_TYPE_REG_DND;
                    }
                    IptMgr.getInstance().setIPTService("", iptServiceType);
                }

                break;
            case R.id.imageButton_cw:
                if(!"false".equals(button.getTag()))
                {
                    showLoginDialog("ipt");
                    if ("Register".equals(button.getTag())) {
                        button.setTag("");
                        iptServiceType = TupCallParam.CALL_E_SERVICE_CALL_TYPE.CALL_E_SERVICE_CALL_TYPE_CALL_WAIT_DEACTIVE;
                    } else {
                        button.setTag("Register");
                        iptServiceType = TupCallParam.CALL_E_SERVICE_CALL_TYPE.CALL_E_SERVICE_CALL_TYPE_CALL_WAIT_ACTIVE;
                    }
                    IptMgr.getInstance().setIPTService("" , iptServiceType);
                }

                break;
            case R.id.imageButton_cfu:
                if (!"false".equals(button.getTag()))
                {
                    showLoginDialog("ipt");
                    dataNumber = et_cfu_num.getText().toString();
                    if( ("".equals(dataNumber) && "".equals(button.getTag())) || ("".equals(dataNumber) && "first".equals(button.getTag()) ) )
                    {
                        new AlertDialog.Builder(this)
                                .setTitle(R.string.ipt_warning)
                                .setMessage(R.string.ipt_warning_number)
                                .setIcon(android.R.drawable.ic_dialog_info)
                                .setPositiveButton(R.string.ipt_sure, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dismissLoginDialog();
                                    }
                                })
                                .show();
                        return;
                    }

                    if ("Register".equals(button.getTag())) {
                        button.setTag("");
                        et_cfu_num.setEnabled(true);
                        iptServiceType = TupCallParam.CALL_E_SERVICE_CALL_TYPE.CALL_E_SERVICE_CALL_TYPE_FORWARD_UNCONDITION_DEACTIVE;
                    } else {
                        button.setTag("Register");
                        et_cfu_num.setEnabled(false);
                        iptServiceType = TupCallParam.CALL_E_SERVICE_CALL_TYPE.CALL_E_SERVICE_CALL_TYPE_FORWARD_UNCONDITION_ACTIVE;
                    }
                    IptMgr.getInstance().setIPTService(dataNumber , iptServiceType);
                }

                break;
            case R.id.imageButton_cfb:
                if (!"false".equals(button.getTag()))
                {
                    showLoginDialog("ipt");
                    dataNumber = et_cfb_num.getText().toString();

                    if( ("".equals(dataNumber) && "".equals(button.getTag())) || ("".equals(dataNumber) && "first".equals(button.getTag()) ) )
                    {
                        new AlertDialog.Builder(this)
                                .setTitle(R.string.ipt_warning)
                                .setMessage(R.string.ipt_warning_number)
                                .setIcon(android.R.drawable.ic_dialog_info)
                                .setPositiveButton(R.string.ipt_sure, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dismissLoginDialog();
                                    }
                                })
                                .show();
                        return;
                    }

                    if ("Register".equals(button.getTag())) {
                        button.setTag("");
                        et_cfb_num.setEnabled(true);
                        iptServiceType = TupCallParam.CALL_E_SERVICE_CALL_TYPE.CALL_E_SERVICE_CALL_TYPE_FORWARD_ONBUSY_DEACTIVE;
                    } else {
                        button.setTag("Register");
                        et_cfb_num.setEnabled(false);
                        iptServiceType = TupCallParam.CALL_E_SERVICE_CALL_TYPE.CALL_E_SERVICE_CALL_TYPE_FORWARD_ONBUSY_ACTIVE;
                    }
                    IptMgr.getInstance().setIPTService(dataNumber , iptServiceType);
                }

                break;
            case R.id.imageButton_cfna:
                if (!"false".equals(button.getTag()))
                {
                    showLoginDialog("ipt");
                    dataNumber = et_cfna_num.getText().toString();

                    if( ("".equals(dataNumber) && "".equals(button.getTag())) || ("".equals(dataNumber) && "first".equals(button.getTag()) ) )
                    {
                        new AlertDialog.Builder(this)
                                .setTitle(R.string.ipt_warning)
                                .setMessage(R.string.ipt_warning_number)
                                .setIcon(android.R.drawable.ic_dialog_info)
                                .setPositiveButton(R.string.ipt_sure, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dismissLoginDialog();
                                    }
                                })
                                .show();
                        return;
                    }

                    if ("Register".equals(button.getTag())) {
                        button.setTag("");
                        et_cfna_num.setEnabled(true);
                        iptServiceType = TupCallParam.CALL_E_SERVICE_CALL_TYPE.CALL_E_SERVICE_CALL_TYPE_FORWARD_NOREPLY_DEACTIVE;
                    } else {
                        button.setTag("Register");
                        et_cfna_num.setEnabled(false);
                        iptServiceType = TupCallParam.CALL_E_SERVICE_CALL_TYPE.CALL_E_SERVICE_CALL_TYPE_FORWARD_NOREPLY_ACTIVE;
                    }
                    IptMgr.getInstance().setIPTService(dataNumber , iptServiceType);
                }


                break;
            case R.id.imageButton_cfnr:
                if (!"false".equals(button.getTag()))
                {
                    showLoginDialog("ipt");
                    dataNumber = et_cfnr_num.getText().toString();

                    if( ("".equals(dataNumber) && "".equals(button.getTag())) || ("".equals(dataNumber) && "first".equals(button.getTag()) ) )
                    {
                        new AlertDialog.Builder(this)
                                .setTitle(R.string.ipt_warning)
                                .setMessage(R.string.ipt_warning_number)
                                .setIcon(android.R.drawable.ic_dialog_info)
                                .setPositiveButton(R.string.ipt_sure, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dismissLoginDialog();
                                    }
                                })
                                .show();
                        return;
                    }

                    if ("Register".equals(button.getTag())) {
                        button.setTag("");
                        et_cfnr_num.setEnabled(true);
                        iptServiceType = TupCallParam.CALL_E_SERVICE_CALL_TYPE.CALL_E_SERVICE_CALL_TYPE_FORWARD_OFFLINE_DEACTIVE;
                    } else {
                        button.setTag("Register");
                        et_cfnr_num.setEnabled(false);
                        iptServiceType = TupCallParam.CALL_E_SERVICE_CALL_TYPE.CALL_E_SERVICE_CALL_TYPE_FORWARD_OFFLINE_ACTIVE;
                    }
                    IptMgr.getInstance().setIPTService(dataNumber , iptServiceType);
                }

                break;

        }
    }

    public void showLoginDialog(String msg) {
        if (null == mDialog) {
            mDialog = new ProgressDialog(this);
        }

        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setTitle(msg);
        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mDialog.show();
    }

    public void dismissLoginDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if ("onSetIptServiceSuc".equals(msg.obj)) {
                switch (msg.what) {
                    case TupCallParam.CALL_E_SERVICE_CALL_TYPE.CALL_E_SERVICE_CALL_TYPE_REG_DND:
                        imageButton_dnd.setImageResource(R.drawable.setting_switch_on);
                        break;
                    case TupCallParam.CALL_E_SERVICE_CALL_TYPE.CALL_E_SERVICE_CALL_TYPE_UNREG_DND:
                        imageButton_dnd.setImageResource(R.drawable.setting_switch_off);
                        break;
                    case TupCallParam.CALL_E_SERVICE_CALL_TYPE.CALL_E_SERVICE_CALL_TYPE_CALL_WAIT_ACTIVE:
                        imageButton_cw.setImageResource(R.drawable.setting_switch_on);
                        break;
                    case TupCallParam.CALL_E_SERVICE_CALL_TYPE.CALL_E_SERVICE_CALL_TYPE_CALL_WAIT_DEACTIVE:
                        imageButton_cw.setImageResource(R.drawable.setting_switch_off);
                        break;
                    case TupCallParam.CALL_E_SERVICE_CALL_TYPE.CALL_E_SERVICE_CALL_TYPE_FORWARD_UNCONDITION_ACTIVE:
                        imageButton_cfu.setImageResource(R.drawable.setting_switch_on);
                        break;
                    case TupCallParam.CALL_E_SERVICE_CALL_TYPE.CALL_E_SERVICE_CALL_TYPE_FORWARD_UNCONDITION_DEACTIVE:
                        imageButton_cfu.setImageResource(R.drawable.setting_switch_off);
                        break;
                    case TupCallParam.CALL_E_SERVICE_CALL_TYPE.CALL_E_SERVICE_CALL_TYPE_FORWARD_ONBUSY_ACTIVE:
                        imageButton_cfb.setImageResource(R.drawable.setting_switch_on);
                        break;
                    case TupCallParam.CALL_E_SERVICE_CALL_TYPE.CALL_E_SERVICE_CALL_TYPE_FORWARD_ONBUSY_DEACTIVE:
                        imageButton_cfb.setImageResource(R.drawable.setting_switch_off);
                        break;
                    case TupCallParam.CALL_E_SERVICE_CALL_TYPE.CALL_E_SERVICE_CALL_TYPE_FORWARD_NOREPLY_ACTIVE:
                        imageButton_cfna.setImageResource(R.drawable.setting_switch_on);
                        break;
                    case TupCallParam.CALL_E_SERVICE_CALL_TYPE.CALL_E_SERVICE_CALL_TYPE_FORWARD_NOREPLY_DEACTIVE:
                        imageButton_cfna.setImageResource(R.drawable.setting_switch_off);
                        break;
                    case TupCallParam.CALL_E_SERVICE_CALL_TYPE.CALL_E_SERVICE_CALL_TYPE_FORWARD_OFFLINE_ACTIVE:
                        imageButton_cfnr.setImageResource(R.drawable.setting_switch_on);
                        break;
                    case TupCallParam.CALL_E_SERVICE_CALL_TYPE.CALL_E_SERVICE_CALL_TYPE_FORWARD_OFFLINE_DEACTIVE:
                        imageButton_cfnr.setImageResource(R.drawable.setting_switch_off);
                        break;

                }
            } else if ("onSetIptServiceFal".equals(msg.obj)) {
                switch (msg.what) {
                    case TupCallParam.CALL_E_SERVICE_CALL_TYPE.CALL_E_SERVICE_CALL_TYPE_REG_DND:
                        Toast.makeText(IptRegisterActivity.this, R.string.ipt_register_dnd_failed, Toast.LENGTH_SHORT).show();
                        break;
                    case TupCallParam.CALL_E_SERVICE_CALL_TYPE.CALL_E_SERVICE_CALL_TYPE_UNREG_DND:
                        Toast.makeText(IptRegisterActivity.this, R.string.ipt_logoff_dnd_failed, Toast.LENGTH_SHORT).show();
                        break;
                    case TupCallParam.CALL_E_SERVICE_CALL_TYPE.CALL_E_SERVICE_CALL_TYPE_CALL_WAIT_ACTIVE:
                        Toast.makeText(IptRegisterActivity.this, R.string.ipt_register_cw_failed, Toast.LENGTH_SHORT).show();
                        break;
                    case TupCallParam.CALL_E_SERVICE_CALL_TYPE.CALL_E_SERVICE_CALL_TYPE_CALL_WAIT_DEACTIVE:
                        Toast.makeText(IptRegisterActivity.this, R.string.ipt_logoff_cw_failed, Toast.LENGTH_SHORT).show();
                        break;
                    case TupCallParam.CALL_E_SERVICE_CALL_TYPE.CALL_E_SERVICE_CALL_TYPE_FORWARD_UNCONDITION_ACTIVE:
                        Toast.makeText(IptRegisterActivity.this, R.string.ipt_register_cfu_failed, Toast.LENGTH_SHORT).show();
                        break;
                    case TupCallParam.CALL_E_SERVICE_CALL_TYPE.CALL_E_SERVICE_CALL_TYPE_FORWARD_UNCONDITION_DEACTIVE:
                        Toast.makeText(IptRegisterActivity.this, R.string.ipt_logoff_cfu_failed, Toast.LENGTH_SHORT).show();
                        break;
                    case TupCallParam.CALL_E_SERVICE_CALL_TYPE.CALL_E_SERVICE_CALL_TYPE_FORWARD_ONBUSY_ACTIVE:
                        Toast.makeText(IptRegisterActivity.this, R.string.ipt_register_cfb_failed, Toast.LENGTH_SHORT).show();
                        break;
                    case TupCallParam.CALL_E_SERVICE_CALL_TYPE.CALL_E_SERVICE_CALL_TYPE_FORWARD_ONBUSY_DEACTIVE:
                        Toast.makeText(IptRegisterActivity.this, R.string.ipt_logoff_cfb_failed, Toast.LENGTH_SHORT).show();
                        break;
                    case TupCallParam.CALL_E_SERVICE_CALL_TYPE.CALL_E_SERVICE_CALL_TYPE_FORWARD_NOREPLY_ACTIVE:
                        Toast.makeText(IptRegisterActivity.this, R.string.ipt_register_cfna_failed, Toast.LENGTH_SHORT).show();
                        break;
                    case TupCallParam.CALL_E_SERVICE_CALL_TYPE.CALL_E_SERVICE_CALL_TYPE_FORWARD_NOREPLY_DEACTIVE:
                        Toast.makeText(IptRegisterActivity.this, R.string.ipt_logoff_cfna_failed, Toast.LENGTH_SHORT).show();
                        break;
                    case TupCallParam.CALL_E_SERVICE_CALL_TYPE.CALL_E_SERVICE_CALL_TYPE_FORWARD_OFFLINE_ACTIVE:
                        Toast.makeText(IptRegisterActivity.this, R.string.ipt_register_cfnr_failed, Toast.LENGTH_SHORT).show();
                        break;
                    case TupCallParam.CALL_E_SERVICE_CALL_TYPE.CALL_E_SERVICE_CALL_TYPE_FORWARD_OFFLINE_DEACTIVE:
                        Toast.makeText(IptRegisterActivity.this, R.string.ipt_logoff_cfnr_failed, Toast.LENGTH_SHORT).show();
                        break;

                }
            }
        }
    };
}
