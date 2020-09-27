package com.example.sos_helpme;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import java.net.URI;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.sos.MESSAGE";

    //声明AMapLocationClient类对象
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = new AMapLocationClientOption();
    //定位结果
    public String Loc_result;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化client
        locationClient = new AMapLocationClient(this.getApplicationContext());
        //设置定位参数
        locationClient.setLocationOption(getDefaultOption());

        // 设置定位监听
        locationClient.setLocationListener(locationListener);

        setContentView(R.layout.activity_main);
        //检查手机、短信发送、定位以及写入权限
        if ((ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.SEND_SMS) ==
                PackageManager.PERMISSION_GRANTED)&&(ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED)&&(ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED)&&(ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.READ_PHONE_STATE) ==
                PackageManager.PERMISSION_GRANTED)){
            Toast.makeText(this, "就绪", Toast.LENGTH_SHORT).show();
        }else if((ActivityCompat.shouldShowRequestPermissionRationale
                    (MainActivity.this,Manifest.permission.SEND_SMS))||(ActivityCompat.shouldShowRequestPermissionRationale
                (MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE))||(ActivityCompat.shouldShowRequestPermissionRationale
                (MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION))||(ActivityCompat.shouldShowRequestPermissionRationale
                (MainActivity.this,Manifest.permission.READ_PHONE_STATE))){
            final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("注意");
            alertDialog.setMessage("这款APP需要定位，短信发送等权限，否则可能无法正常使用.");
            alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    alertDialog.dismiss();
                }
            });
            alertDialog.show();
        }else{//进入申请流程
            ActivityCompat.requestPermissions(MainActivity.this,new String[]
                    {Manifest.permission.SEND_SMS,Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.READ_PHONE_STATE},1);
        }
    }



    @Override
    protected void onResume() {

        super.onResume();
        Button sos_button = findViewById(R.id.SOS_button);
        Button github_button = findViewById(R.id.github_button);
        if (ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.SEND_SMS) ==
                PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "就绪", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "本APP可能无法正常使用，请检查相关权限", Toast.LENGTH_SHORT).show();
        }
        sos_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "定位中...", Toast.LENGTH_SHORT).show();
                startLocation();
                stopLocation();
                startLocation();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                sendMSG();
                stopLocation();
                Toast.makeText(MainActivity.this, "完成", Toast.LENGTH_SHORT).show();

            }
        });
        github_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://github.com/welllhq/SOS_helpme");
                Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyLocation();
    }

    @Override//权限请求回调
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "已授权", Toast.LENGTH_SHORT).show();
                } else {
                    final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                    alertDialog.setTitle("Help?");
                    alertDialog.setMessage("这款APP需要定位，短信发送等权限，否则可能无法正常使用," +
                            "请给予授权.");
                    alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Go to set", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent sintent = new Intent(Settings.ACTION_SETTINGS);
                            startActivity(sintent);
                            alertDialog.cancel();
                        }
                    });
                    alertDialog.show();
                }
        }
    }
    //跳转至添加紧急联系人与紧急信息的activity
    public void addContacts(View view){
        Intent intent = new Intent(MainActivity.this,Add_contacts.class);
        startActivity(intent);
    }
    //跳转至设置定时器的activity
    public void setTime(View view){
        Intent intent = new Intent(MainActivity.this,Timer_activity.class);
        startActivity(intent);
    }
    //发送预先编辑短信
    public void sendMSG(){
        //声明SharedPreferences对象
        final SharedPreferences sp =  getSharedPreferences("urgentMSG",MODE_PRIVATE);
        String phoneMSG = sp.getString("urgentMSG",null);
        String phoneNUM1 = sp.getString("urgent_contact1",null);
        String phoneNUM2 = sp.getString("urgent_contact2",null);
        String phoneNUM3 = sp.getString("urgent_contact3",null);
        try{
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNUM1,null,phoneMSG+"Location："+Loc_result,null,null);
            smsManager.sendTextMessage(phoneNUM2,null,phoneMSG+"Location："+Loc_result,null,null);
            smsManager.sendTextMessage(phoneNUM3,null,phoneMSG+"Location："+Loc_result,null,null);
            Toast.makeText(this, "短信发送成功", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(this, "发送失败，请检查权限或紧急联系人与信息", Toast.LENGTH_SHORT).show();
        }
    }
    //高德定位参数设置
    private AMapLocationClientOption getDefaultOption(){
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(true);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        return mOption;
    }
    //高德定位监听
    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation loc) {
            if (null != loc) {
                //解析定位结果
                Loc_result= loc.getAddress();
            } else {
                Loc_result = "定位获取失败";
            }
        }
    };
    //开始定位
    private void startLocation(){
        // 设置定位参数
        locationClient.setLocationOption(locationOption);
        // 启动定位
        locationClient.startLocation();
    }
    private void stopLocation(){
        // 停止定位
        locationClient.stopLocation();
    }
    //销毁定位
    private void destroyLocation(){
        if (null != locationClient) {
            /*
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
    }

}
 class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
       /* MainActivity mainActivity = new MainActivity();
        mainActivity.startLocation();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mainActivity.sendMSG();
        mainActivity.stopLocation();*/
        Toast.makeText(context, "已收到广播", Toast.LENGTH_SHORT).show();
    }
}