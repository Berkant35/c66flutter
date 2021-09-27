package com.example.elterminali;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.rscja.deviceapi.RFIDWithUHFUART;
import com.rscja.deviceapi.entity.UHFTAGInfo;
import java.util.ArrayList;
import java.util.List;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugins.GeneratedPluginRegistrant;


public class MainActivity extends FlutterActivity {
    public RFIDWithUHFUART mReader;
    public UHFTAGInfo uhftagInfo;
    boolean loopFlag = false;
    List<String> allEPC = new ArrayList<String>();
    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);
        GeneratedPluginRegistrant.registerWith(flutterEngine);
        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), "mainChannel").setMethodCallHandler(
                (call, result) -> {
                    initUHF();
                    loopFlag = call.method.equals("getRFID");
                    String strTid;
                    String strResult;
                    UHFTAGInfo res;
                    mReader.startInventoryTag();
                    new TagThread().start();
                    if(mReader.stopInventory()){
                        result.success(allEPC);
                    }
                }
        );
    }
    class TagThread extends Thread {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public void run() {
            WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            SupplicantState supplicantState= wifiInfo.getSupplicantState();


            int wifiRssi = wifiInfo.getRssi();
            int describeContetns=wifiInfo.describeContents();
            int getFrequency=wifiInfo.getFrequency();
            int getIpAddress=wifiInfo.getIpAddress();
            int getLinkSpeed=wifiInfo.getLinkSpeed();
            int describeContents =  supplicantState.describeContents();
            String getSSID = wifiInfo.getSSID();

            Log.i("describeContents", String.valueOf(describeContetns));
            Log.i("rssi", String.valueOf(wifiRssi));
            Log.i("getFrequency", String.valueOf(getFrequency));
            Log.i("getIpAddress", String.valueOf(getIpAddress));
            Log.i("getLinkSpeed", String.valueOf(getLinkSpeed));
            Log.i("getSSID", String.valueOf(getSSID));
            Log.i("describeContents", String.valueOf(describeContents));

            String strTid;
            String strResult;
            UHFTAGInfo res = null;
            while (loopFlag) {
                res = mReader.readTagFromBuffer();
                if (res != null) {
                    strTid = res.getTid();
                    if (strTid.length() != 0 && !strTid.equals("0000000" +
                            "000000000") && !strTid.equals("000000000000000000000000")) {
                        strResult = "TID:" + strTid + "\n";
                    } else {
                        strResult = "";
                    }
                    Log.i("data","EPC:"+res.getEPC()+"|"+strResult);
                    if(!allEPC.contains(res.getEPC())){
                        allEPC.add(res.getEPC());
                    }

                }
            }
        }
    }
    public void initUHF() {
        try {
            mReader = RFIDWithUHFUART.getInstance();
            mReader.init();
        } catch (Exception exception) {
            Log.d("mReader HATA : ", exception.toString());
        }
    }
}
