package com.android.tvremoteplay;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.tvremoteplay.server.RemoteServer;
import com.android.tvremoteplay.adb.AdbHelper;

import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener {

    private ImageView qrCodeImage;
    private TextView addressView;
    private EditText dlnaNameText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        qrCodeImage = this.findViewById(R.id.ivQRCode);
        addressView = this.findViewById(R.id.tvAddress);
        dlnaNameText = this.findViewById(R.id.etDLNAName);

        this.setTitle(this.getResources().getString( R.string.app_name) + "  V" + AppPackagesHelper.getCurrentPackageVersion(this));
        dlnaNameText.setText(DLNAUtils.getDLNANameSuffix(this.getApplicationContext()));

        final Intent intent = new Intent(IMEService.ACTION);
        final Intent eintent = new Intent(createExplicitFromImplicitIntent(this,intent));
        startService(eintent);
        if(!Environment.isDefaultIME(this)) {
            if (AdbHelper.getInstance() == null) AdbHelper.createInstance();
        }
        Environment.toast(getApplicationContext(), "服务启动，稍后可尝试访问控制端页面");

        refreshQRCode();

//        XLVideoPlayActivity.intentTo(XLVideoPlayActivity.class, this, "https://135zyv4.xw0371.com/2018/06/26/mSQ3D0E2gPZxQAb2/playlist.m3u8", "test", 0);
    }
    /***
     * Android L (lollipop, API 21) introduced a new problem when trying to invoke implicit intent,
     * "java.lang.IllegalArgumentException: Service Intent must be explicit"
     *
     * If you are using an implicit intent, and know only 1 target would answer this intent,
     * This method will help you turn the implicit intent into the explicit form.
     *
     * Inspired from SO answer: http://stackoverflow.com/a/26318757/1446466
     * @param context
     * @param implicitIntent - The original implicit intent
     * @return Explicit Intent created from the implicit original intent
     */
    public static Intent createExplicitFromImplicitIntent(Context context, Intent implicitIntent) {
        // Retrieve all services that can match the given intent
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);

        // Make sure only one match was found
        if (resolveInfo == null || resolveInfo.size() != 1) {
            return null;
        }

        // Get component info and create ComponentName
        ResolveInfo serviceInfo = resolveInfo.get(0);
        String packageName = serviceInfo.serviceInfo.packageName;
        String className = serviceInfo.serviceInfo.name;
        ComponentName component = new ComponentName(packageName, className);

        // Create a new intent. Use the old one for extras and such reuse
        Intent explicitIntent = new Intent(implicitIntent);

        // Set the component to be explicit
        explicitIntent.setComponent(component);

        return explicitIntent;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
//            case R.id.btnUseIME:
//                openInputMethodSettings();
//                if(Environment.isEnableIME(this)){
//                    Environment.toast(getApplicationContext(), "太棒了，您已经激活启用了" + getString(R.string.keyboard_name) +"输入法！");
//                }
//                break;
//            case R.id.btnSetIME:
//                if(!Environment.isEnableIME(this)) {
//                    Environment.toast(getApplicationContext(), "抱歉，请您先激活启用" + getString(R.string.keyboard_name) +"输入法！");
//                    openInputMethodSettings();
//                    if(!Environment.isEnableIME(this)) return;
//                }
//                try {
//                    ((InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE)).showInputMethodPicker();
//                }catch (Exception ignored) {
//                    Environment.toast(getApplicationContext(), "抱歉，无法设置为系统默认输入法，请手动启动服务！");
//                }
//                if(Environment.isDefaultIME(this)){
//                    Environment.toast(getApplicationContext(), "太棒了，" + getString(R.string.keyboard_name) +"已是系统默认输入法！");
//                }
//                break;
//            case R.id.btnStartService:
//                startService(new Intent(IMEService.ACTION));
//                if(!Environment.isDefaultIME(this)) {
//                    if (AdbHelper.getInstance() == null) AdbHelper.createInstance();
//                }
//                Environment.toast(getApplicationContext(), "服务已手动启动，稍后可尝试访问控制端页面");
//                break;
            case R.id.btnSetDLNA:
                DLNAUtils.setDLNANameSuffix(this.getApplicationContext(), dlnaNameText.getText().toString());
                break;
        }
        refreshQRCode();
    }
    private void openInputMethodSettings(){
        try {
            this.startActivityForResult(new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS), 0);
        }catch (Exception ignored){
            Environment.toast(getApplicationContext(), "抱歉，无法激活启用输入法，请手动启动服务！");
        }
    }
    private void refreshQRCode(){
        String address = RemoteServer.getServerAddress(this);
        addressView.setText(address);
        qrCodeImage.setImageBitmap(QRCodeGen.generateBitmap(address, 150, 150));
    }




}
