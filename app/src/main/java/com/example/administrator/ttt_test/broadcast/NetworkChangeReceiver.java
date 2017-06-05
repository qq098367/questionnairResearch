package com.example.administrator.ttt_test.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkChangeReceiver extends BroadcastReceiver {
    private boolean haveNetwork=false;
    private Context context;

    public boolean isHaveNetwork() {
        return haveNetwork;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context=context;
        ConnectivityManager manager= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=manager.getActiveNetworkInfo();
        if(networkInfo!=null){
            haveNetwork=networkInfo.isAvailable();
        }
    }

}
