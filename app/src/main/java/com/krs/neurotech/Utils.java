package com.krs.neurotech;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

class Utils {

    static boolean checkWifiOnAndConnected(Context context) {
        WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifiMgr.isWifiEnabled()) { // Wi-Fi adapter is ON
            WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
            // Not connected to an access point
            return wifiInfo.getNetworkId() != -1;// Connected to an access point
        } else {
            return false; // Wi-Fi adapter is OFF
        }
    }

}
