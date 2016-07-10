package com.codetroopers.trai.Receivers;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ConnectivityChangeReceiver extends BroadcastReceiver {

    String file_name = "NetworkHistory.csv";

    @Override
    public void onReceive(Context context, Intent intent) {
        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();
        if(isNetworkConnected(context)) {
            String ntype = new NetworkType().getNetworkType(context);
            Toast toast;
            toast = Toast.makeText(context, "The Signal is " + ntype, Toast.LENGTH_SHORT);
            toast.show();
            try {
            FileWriter fw = new FileWriter(new File(context.getFilesDir(), file_name),true);
            fw.write(ts+"," +ntype+"\n");
            fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            String ntype = new String("Not Connected");
            Toast toast;
            toast = Toast.makeText(context, "The Signal is " + ntype, Toast.LENGTH_SHORT);
            toast.show();
            try {
                FileWriter fw = new FileWriter(new File(context.getFilesDir(), file_name),true);
                fw.write(ts+"," +"NC"+"\n");
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }
}
